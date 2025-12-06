import examples.SamplesGenerator
import optimizer.CMAESOptimizerImpl

fun main() {
    runCMAESExperiment()
}


fun runCMAESExperiment() {

    // ============================================================
    // 1. Generate Training Dataset
    // ============================================================

    val numColors = 3
    val step = 0.0
    val numberOfSamples = 10

    val samplesGenerator = SamplesGenerator()

    val trainingDataPath = generateOutputPath(
        algo = "",
        numColors = numColors,
        step = step,
        dataType = "training"
    )

    samplesGenerator.generateTrainingDataset(
        outputPath = trainingDataPath,
        numColors = numColors,
        step = step,
        numberOfSamples = numberOfSamples
    )


    // ============================================================
    // 2. Define CMA-ES Parameter Sweep
    // ============================================================

    val populationMultipliers = listOf(5, 10, 20)
    val sigmas = listOf(0.2, 0.3, 0.5)
    val diagonalOnlyValues = listOf(10, 20)
    val feasibleCheckCounts = listOf(5, 10)
    val stopFitnessValues = listOf(1e-3, 1e-2)


    // Generate all parameter tuples
    val allParameterTuples = cartesianProduct(
        listOf(
            populationMultipliers,
            sigmas,
            diagonalOnlyValues,
            feasibleCheckCounts,
            stopFitnessValues
        )
    )

    // Convert tuples → maps
    val parameterSets = allParameterTuples.map { tuple ->
        mapOf(
            "populationMultiplier" to tuple[0],
            "sigma" to tuple[1],
            "diagonalOnly" to tuple[2],
            "checkFeasibleCount" to tuple[3],
            "stopFitness" to tuple[4]
        )
    }


    // ============================================================
    // 3. Run CMA-ES for Every Parameter Combination
    // ============================================================

    parameterSets.forEachIndexed { index, params ->

        val fileSuffix = paramFileSuffix(params)

        val resultsDataPath = generateOutputPath(
            algo = "CMA-ES-$fileSuffix",
            numColors = numColors,
            step = step,
            dataType = "results"
        )

        println("\n=== Running CMA-ES with parameter set #$index ===")
        println("Params: $params")
        println("Output file: $resultsDataPath")

        samplesGenerator.generateOptimizationSamples(
            trainingDataPath = trainingDataPath,
            resultOutputPath = resultsDataPath,
            optimizerName = "CMA-ES",
            errorName = "DeltaE2000",
            includeSparsityPenalty = true,
            initialGuessType = "Uniform",
            optimizationParameters = params,
            numberOfSamples = numberOfSamples
        )
    }

    println("\n=== Experiment complete! ===")
}


// ============================================================
// Helpers
// ============================================================

fun generateOutputPath(
    algo: String? = "",
    numColors: Int,
    step: Double,
    dataType: String
): String {
    val stepStr = if (step == 0.0) "random" else step.toString()
    return "C:\\Users\\safii\\IdeaProjects\\CI-Colors\\src\\main\\kotlin\\datasets\\$dataType-$algo-$numColors-colors-$stepStr-step.csv"
}

fun <T> cartesianProduct(lists: List<List<T>>): List<List<T>> =
    lists.fold(listOf(listOf<T>())) { acc, list ->
        acc.flatMap { accItem -> list.map { accItem + it } }
    }


// Create a descriptive filename suffix based on parameters
fun paramFileSuffix(params: Map<String, Any>): String =
    params.entries.joinToString("-") { (k, v) ->
        "$k-$v"
    }.replace(".", "_")   // avoid decimals in filenames
