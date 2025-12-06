package hyperparameter

import examples.SamplesGenerator

/**
 * Generic objective function for hyperparameter optimization.
 * Evaluates hyperparameters by running the inner optimizer and measuring performance.
 */
class HyperparameterObjective(
    private val trainingDataPath: String,
    private val numSamples: Int,
    private val tempOutputDir: String,
    private val innerOptimizerName: String,
    private val hyperparameters: List<HyperparameterConfig>
) {

    /**
     * Evaluate a set of hyperparameter values.
     *
     * @param params Array of continuous values (one per hyperparameter)
     * @return Mean error achieved by the inner optimizer with these parameters
     */
    fun evaluate(params: DoubleArray): Double {

        // Transform continuous values to actual parameter values
        val paramMap = hyperparameters.mapIndexed { i, config ->
            config.name to config.transform(params[i])
        }.toMap()

        val outputFile = "$tempOutputDir/run-${System.currentTimeMillis()}.csv"

        val gen = SamplesGenerator()
        gen.generateOptimizationSamples(
            trainingDataPath = trainingDataPath,
            resultOutputPath = outputFile,
            optimizerName = innerOptimizerName,
            errorName = "DeltaE2000",
            includeSparsityPenalty = true,
            initialGuessType = "Uniform",
            optimizationParameters = paramMap,
            numberOfSamples = numSamples
        )

        val sample = HyperparameterResultReader.loadSampleFromFile(outputFile)

        return sample.meanError
    }
}
