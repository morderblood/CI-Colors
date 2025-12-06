import examples.SamplesGenerator

fun main() {

    /**
     * Generating training samples.
     */
    val numColors = 3
    val step = 0.0
    val numberOfSamples = 10

    val trainingDataPath = generateOutputPath(
        numColors=numColors,
        step=step,
        dataType="training")

    val samplesGenerator = SamplesGenerator()
    samplesGenerator.generateTrainingDataset(
        outputPath=trainingDataPath,
        numColors=numColors,
        step=step,
        numberOfSamples=numberOfSamples)

    /**
     * Setting optimization parameters.
     */
    val optimizerParameterMap = mutableMapOf<String, Map<String, Any>>()

    /**
     * BOBYQA
     */
    var optimizerName = "BOBYQA"
    var optimizationParameters = mutableMapOf<String, Any>()
    optimizationParameters["maxEvaluations"] = 1000
    optimizationParameters["numberOfInterpolationPoints"] = 77
    optimizerParameterMap[optimizerName] = optimizationParameters

    /**
     * CMAES
     */
    optimizerName = "CMA-ES"
    optimizationParameters = mutableMapOf<String, Any>()
    optimizationParameters["maxEvaluations"] = 10000
    optimizationParameters["populationMultiplier"] = 10
    optimizationParameters["stopFitness"] = 1e-3
    optimizationParameters["sigma"] = 0.3
    optimizationParameters["diagonalOnly"] = 10
    optimizerParameterMap[optimizerName] = optimizationParameters

    /**
     * Generating optimization results for each optimizer.
     */
    for ((algoName, parameters) in optimizerParameterMap) {
        println("Generating results for $algoName optimizer...")
        val resultsDataPath = generateOutputPath(algoName, numColors, step, "results")

        samplesGenerator.generateOptimizationSamples(
            trainingDataPath = trainingDataPath,
            resultOutputPath = resultsDataPath,
            optimizerName = algoName,
            errorName = "DeltaE2000",
            includeSparsityPenalty = true,
            initialGuessType = "Uniform",
            optimizationParameters = parameters,
            numberOfSamples = numberOfSamples
        )
    }
}

fun generateOutputPath(algo: String? = "", numColors: Int, step: Double, dataType: String): String {
    val stepStr = if (step == 0.0) "random" else step.toString()
    return "C:\\Users\\safii\\IdeaProjects\\CI-Colors\\src\\main\\kotlin\\datasets\\$dataType-$algo-$numColors-colors-$stepStr-step.csv"
}
