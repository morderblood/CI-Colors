package hyperparameter

import examples.SamplesGenerator

class CMAESHyperparameterObjective(
    private val trainingDataPath: String,
    private val numSamples: Int,
    private val tempOutputDir: String
) {

    fun evaluate(params: DoubleArray): Double {

        val populationMultiplier = params[0].toInt().coerceAtLeast(1)
        val sigma                = params[1].coerceAtLeast(1e-6)
        val diagonalOnly         = params[2].toInt().coerceAtLeast(0)
        val feasibleCount        = params[3].toInt().coerceAtLeast(0)
        val stopFitness          = params[4].coerceAtLeast(1e-12)

        val paramMap = mapOf(
            "populationMultiplier" to populationMultiplier,
            "sigma" to sigma,
            "diagonalOnly" to diagonalOnly,
            "checkFeasibleCount" to feasibleCount,
            "stopFitness" to stopFitness
        )

        val outputFile = "$tempOutputDir/run-" +
                "${System.currentTimeMillis()}.csv"

        val gen = SamplesGenerator()
        gen.generateOptimizationSamples(
            trainingDataPath = trainingDataPath,
            resultOutputPath = outputFile,
            optimizerName = "CMA-ES",
            errorName = "DeltaE2000",
            includeSparsityPenalty = true,
            initialGuessType = "Uniform",
            optimizationParameters = paramMap,
            numberOfSamples = numSamples
        )

        // After inner CMA-ES run, compute mean DeltaE error
        val sample = HyperparameterResultReader.loadSampleFromFile(outputFile)

        return sample.meanError
    }
}
