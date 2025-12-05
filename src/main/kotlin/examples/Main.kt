package examples

import data.Palette
import evaluation.DataSetItemReader
import evaluation.DataSetItemWriter
import evaluation.OptimizationResultData
import functional.error.MixingErrorFactory
import functional.initialGuess.InitialGuessGeneratorFactory
import functional.mixer.MixboxColorMixer
import functional.normalizer.ProportionsNormalizer
import goal.Goal
import optimizer.OptimizerFactory
import penalty.Penalty
import penalty.SparsityPenalty

fun main() {

    val numColors = 3
    val step = 0.25

    val trainingDataPath = "C:\\Users\\safii\\IdeaProjects\\CI-Colors\\src\\main\\kotlin\\datasets\\training_set_$numColors-colors-$step-step_for_nsgaII.csv"

    GenerateTrainingSet.generateDataset(trainingDataPath, numColors, step)

    val algos = arrayOf("BOBYQA", "SMSEMOA", "Nelder-Mead", "Powell", "NSGAII", "CMA-ES")

    val samplesGenerator = SamplesGenerator()
    for (algo in algos) {
        println("Generating results for $algo...")
        val resultOutputPath = generateOutputPath(algo, numColors, step, "result")
        samplesGenerator.generateOptimizationSamples(
            trainingDataPath,
            resultOutputPath,
            algo,
            "DeltaE2000",
            true,
            numberOfSamples = 1,
            useOptimizerFactoryDefaults = true,
            optimizationParameters = mapOf("populationSize" to 100))
    }
}
fun generateOutputPath(algo: String, numColors: Int, step: Double, dataType: String): String {
    return "C:\\Users\\safii\\IdeaProjects\\CI-Colors\\src\\main\\kotlin\\datasets\\$dataType-$algo-$numColors-colors-$step-step.csv"
}