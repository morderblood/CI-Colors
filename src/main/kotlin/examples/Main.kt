package examples

import data.Palette
import evaluation.DataSetItemReader
import evaluation.DataSetItemWriter
import evaluation.OptimizationResultData
import functional.error.DeltaE2000
import functional.mixer.MixboxColorMixer
import functional.normalizer.ProportionsNormalizer
import goal.Goal
import optimizer.CMAESOptimizerImpl
import penalty.Penalty
import penalty.SparsityPenalty

import kotlin.reflect.full.memberProperties

fun main() {

    val numColors = 4
    val step = 0.2

    val trainingDataPath = "C:\\Users\\safii\\IdeaProjects\\CI-Colors\\src\\main\\kotlin\\datasets\\training_set_$numColors-colors-$step-step.csv"
    val resultOutputPath = "C:\\Users\\safii\\IdeaProjects\\CI-Colors\\src\\main\\kotlin\\datasets\\results_cma_es_$numColors-colors-$step-step.csv"

    GenerateTrainingSet.generateDataset(trainingDataPath, numColors, step)

    generateOptimizations(trainingDataPath, resultOutputPath)
}

fun generateOptimizations(trainingDataPath: String, resultOutputPath: String) {
    val normalizer = ProportionsNormalizer()
    val mixer = MixboxColorMixer()
    val mixingError = DeltaE2000()
    val penalties = mutableListOf<Penalty>()
    penalties.add(SparsityPenalty(threshold = 0.01, penaltyPerColor = 1.0))
    val palette = Palette.allColors
    val optimizer = CMAESOptimizerImpl()

    val dataSet = DataSetItemReader.readCSV(trainingDataPath)

    val headers = OptimizationResultData::class.memberProperties.map { it.name }
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