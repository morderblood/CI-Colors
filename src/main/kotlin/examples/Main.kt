package examples

import data.Palette
import evaluation.DataSetItemReader
import evaluation.DataSetItemWriter
import evaluation.OptimizationResultData
import functional.error.DeltaE2000
import functional.mixer.MixboxColorMixer
import functional.normalizer.ProportionsNormalizer
import goal.Goal
import optimizer.MOEAFactory
import optimizer.MOEAOptimizerImp
import optimizer.NSGAIIOptimizerImpl
import penalty.Penalty
import penalty.SparsityPenalty

fun main() {

    val numColors = 3
    val step = 0.25

    val trainingDataPath = "C:\\Users\\safii\\IdeaProjects\\CI-Colors\\src\\main\\kotlin\\datasets\\training_set_$numColors-colors-$step-step_for_nsgaII.csv"

    GenerateTrainingSet.generateDataset(trainingDataPath, numColors, step)

    //val algos = arrayOf("BOBYQA", "SMSEMOA", "Nelder-Mead", "Powell", "NSGAII", "CMA-ES")
    val algos = arrayOf("NSGAII")
    for (algo in algos) {
        println("Generating results for $algo...")
        val resultOutputPath = "C:\\Users\\safii\\IdeaProjects\\CI-Colors\\src\\main\\kotlin\\datasets\\results_$algo-$numColors-colors-$step-step.csv"
        generateOptimizations(trainingDataPath, resultOutputPath, algo)
    }
}

fun generateOptimizations(trainingDataPath: String, resultOutputPath: String, optimizerName: String) {
    val normalizer = ProportionsNormalizer()
    val mixer = MixboxColorMixer()
    val mixingError = DeltaE2000()
    val penalties = mutableListOf<Penalty>()
    penalties.add(SparsityPenalty(threshold = 0.01, penaltyPerColor = 1.0))
    val palette = Palette.allColors
    val optimizer = MOEAFactory().createOptimizer(optimizerName)

    val dataSet = DataSetItemReader.readCSV(trainingDataPath)

    val headers = "targetLab,resultLab,targetWeights,resultWeights,optimizer,numberOfEvaluations,mixingError,penalties,normalizer,initialGuessType".split(",")
    DataSetItemWriter.writeHeader(resultOutputPath, headers)

    for (data in dataSet) {
        val goal = Goal(
            palette,
            data.targetLab,
            penalties,
            mixingError,
            normalizer,
            mixer
        )

        val initialGuess = DoubleArray(palette.size) { 1.0 / palette.size }

        val result = optimizer.optimize(goal, initialGuess)

        val resultLab = mixer.mixColors(result.weights, palette)

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
            initialGuessType = "Uniform",
        )

        DataSetItemWriter.appendLine(resultData, resultOutputPath)
    }
}