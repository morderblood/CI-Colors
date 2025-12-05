package examples

import data.Palette
import evaluation.DataSetItemReader
import evaluation.DataSetItemWriter
import evaluation.OptimizationResultData
import evaluation.TrainingSetCreator
import functional.error.MixingErrorFactory
import functional.initialGuess.InitialGuessGeneratorFactory
import functional.mixer.MixboxColorMixer
import functional.normalizer.ProportionsNormalizer
import goal.Goal
import optimizer.OptimizerFactory
import penalty.Penalty
import penalty.SparsityPenalty

/* Generates optimization results for a given dataset. */
class SamplesGenerator {
    fun generateTrainingDataset(
        outputPath: String,
        numColors: Int = 5,
        step: Double = 0.2
    ) {

        val palette = Palette.allColors
        val mixer = MixboxColorMixer()

        val creator = TrainingSetCreator(mixer)
        //val dataSet = creator.createKColorDataSet(palette, numColors, step, mixer, outputPath)
        val dataSet = creator.createRandom3ColorDataSet(palette, mixer, outputPath, 10)
    }
    /**
     * Generate optimization results for a given dataset.
     *
     * @param trainingDataPath path to the training dataset
     * @param resultOutputPath path to the output file
     * @param optimizerName name of the optimizer to use: NSGAII, SPEA2, CMA-ES, BOBYQA, Powell, Nelder-Mead, SMSEMOA
     * @param errorName name of the mixing error to use: deltaE76, deltaE2000
     * @param includeSparsityPenalty whether to include sparsity penalty in the goal function
     * @param useOptimizerFactoryDefaults whether to use the factory default parameters to create the optimizer.
     * @param initialGuessType type of initial guess to use: Uniform, Random, Similar
     * @param optimizationParameters additional parameters to pass to the optimizer
     */
    fun generateOptimizationSamples(
        trainingDataPath: String,
        resultOutputPath: String,
        optimizerName: String = "CMA-ES",
        errorName: String = "DeltaE2000",
        includeSparsityPenalty: Boolean = true,
        useOptimizerFactoryDefaults: Boolean = true,
        initialGuessType: String = "Uniform",
        optimizationParameters: Map<String, Any> = emptyMap(),
        numberOfSamples: Int = 100
    ) {
        val optimizer = OptimizerFactory.createOptimizer(optimizerName, optimizationParameters)

        val mixingError = MixingErrorFactory.createMixingError(errorName)

        val normalizer = ProportionsNormalizer()
        val colorMixer = MixboxColorMixer()

        val penalties = mutableListOf<Penalty>()
        if (includeSparsityPenalty) penalties.add(SparsityPenalty(threshold = 0.01, penaltyPerColor = 1.0))

        val palette = Palette.allColors

        val dataSet = DataSetItemReader.readCSV(trainingDataPath)

        val headers = "targetLab,resultLab,targetWeights,resultWeights,optimizer,numberOfEvaluations,mixingError,penalties,normalizer,initialGuessType".split(",")
        DataSetItemWriter.writeHeader(resultOutputPath, headers)

        var iteration = 0
        for (data in dataSet) {
            if (iteration >= numberOfSamples) break

            val goal = Goal(
                palette,
                data.targetLab,
                penalties,
                mixingError,
                normalizer,
                colorMixer
            )

            val initialGuess = InitialGuessGeneratorFactory.createInitialGuessGenerator(
                generatorName = initialGuessType,
                palette = palette,
                targetLab = data.targetLab
            ).generateInitialGuess(dimensions = palette.size)

            val result = optimizer.optimize(goal, initialGuess)

            val resultLab = colorMixer.mixColors(result.weights, palette)

            val resultData = OptimizationResultData(
                targetLab = data.targetLab,
                resultLab = resultLab,
                targetWeights = data.weights,
                resultWeights = result.weights,
                optimizer = optimizer,
                numberOfEvaluations = result.evaluations,
                mixingError = mixingError,
                penalties = penalties,
                normalizer = normalizer,
                initialGuessType = initialGuessType,
            )

            DataSetItemWriter.appendLine(resultData, resultOutputPath)
            iteration++
        }
    }
}