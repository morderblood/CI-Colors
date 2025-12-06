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
import kotlin.reflect.full.primaryConstructor


/* Generates optimization results for a given dataset.
*
* @param outputPath path to the output file
* @param numColors number of colors to mix in the dataset
* @param step step-size for color mixing: 0.0 for random weights
* @param numberOfSamples number of samples to generate
* */
class SamplesGenerator {
    fun generateTrainingDataset(
        outputPath: String,
        numColors: Int = 5,
        step: Double = 0.2,
        numberOfSamples: Int = 10
    ) {

        val palette = Palette.allColors
        val mixer = MixboxColorMixer()

        val creator = TrainingSetCreator(mixer)
        if (step > 0.0) {
            creator.createKColorDataSet(palette, numColors, step, mixer, outputPath)
        } else {
            creator.createRandom3ColorDataSet(palette, mixer, outputPath, numColors, numberOfSamples)
        }
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
        initialGuessType: String = "Uniform",
        optimizationParameters: Map<String, Any>? = emptyMap(),
        numberOfSamples: Int = 100
    ) {
        val optimizer = OptimizerFactory.createOptimizer(optimizerName, optimizationParameters!!)

        val mixingError = MixingErrorFactory.createMixingError(errorName)

        val normalizer = ProportionsNormalizer()
        val colorMixer = MixboxColorMixer()

        val penalties = mutableListOf<Penalty>()
        if (includeSparsityPenalty) penalties.add(SparsityPenalty(threshold = 0.01, penaltyPerColor = 20.0))

        val palette = Palette.allColors

        val dataSet = DataSetItemReader.readCSV(trainingDataPath)

        val allParameterKeys = optimizationParameters.keys

        var headers = OptimizationResultData::class.primaryConstructor!!
            .parameters
            .map { it.name!! }
            .filter { it != "optimizationParameters" }

        val parameterHeaders = allParameterKeys

        headers += parameterHeaders

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
            val initialLab = colorMixer.mixColors(initialGuess, palette)

            val startTime = System.nanoTime()
            val result = optimizer.optimize(goal, initialGuess)
            val runtimeMs = (System.nanoTime() - startTime) / 1_000_000

            val resultLab = colorMixer.mixColors(result.weights, palette)

            val resultData = OptimizationResultData(
                targetLab = data.targetLab,
                resultLab = resultLab,
                initialLab = initialLab,
                targetWeights = data.weights,
                resultWeights = result.weights,
                initialWeights = initialGuess,
                optimizerName = optimizerName,
                numberOfEvaluations = result.evaluations,
                mixingErrorName = errorName,
                penalties = penalties,
                normalizer = normalizer,
                initialGuessType = initialGuessType,
                runtimeMs = runtimeMs,
                converged = result.converged,
                optimizationParameters = optimizationParameters
            )

            DataSetItemWriter.appendLine(resultData, resultOutputPath)
            iteration++
        }
    }
}