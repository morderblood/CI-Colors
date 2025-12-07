import data.Palette
import examples.SamplesGenerator
import functional.mixer.MixboxColorMixer
import hyperparameter.HyperparameterConfig
import hyperparameter.HyperparameterOptimizer
import kotlin.math.pow

fun main() {
    //Run CMA-ES with specific parameters
    runCMAESWithParameters(
        parameters = mapOf(
            "populationMultiplier" to 12,
            "sigma" to 0.19961484447526043,
            "diagonalOnly" to 13,
            "checkFeasibleCount" to 13,
            "stopFitness" to 5.42034391407451E-4
        )
    )
    //runCMAEHyperoptimization()
}

/**
 * Run CMA-ES optimization with specific parameters (no hyperparameter optimization).
 *
 * @param trainingDataPath Path to training dataset
 * @param outputPath Path to save results
 * @param numSamples Number of optimization samples to run
 * @param parameters CMA-ES parameters to use
 */
fun runCMAESWithParameters(
    trainingDataPath: String = "C:\\Users\\safii\\IdeaProjects\\CI-Colors\\src\\main\\kotlin\\datasets\\training--3-colors-random-step.csv",
    outputPath: String = "C:\\Users\\safii\\IdeaProjects\\CI-Colors\\src\\main\\kotlin\\datasets\\results\\cmaes-results-similar.csv",
    numSamples: Int = 50,
    parameters: Map<String, Any> = mapOf(
        "populationMultiplier" to 10,
        "sigma" to 0.3,
        "diagonalOnly" to 10,
        "checkFeasibleCount" to 10,
        "stopFitness" to 0.001
    )
) {
    println("=".repeat(80))
    println("Running CMA-ES with specified parameters")
    println("Training data: $trainingDataPath")
    println("Output: $outputPath")
    println("Number of samples: $numSamples")
    println("Parameters: $parameters")
    println("=".repeat(80))
    println()

    val generator = SamplesGenerator()

    try {
        generator.generateOptimizationSamples(
            trainingDataPath = trainingDataPath,
            resultOutputPath = outputPath,
            optimizerName = "CMA-ES",
            errorName = "DeltaE2000",
            includeSparsityPenalty = true,
            initialGuessType = "Uniform",
            optimizationParameters = parameters,
            numberOfSamples = numSamples
        )

        println()
        println("=".repeat(80))
        println("✓ CMA-ES optimization completed successfully!")
        println("Results saved to: $outputPath")
        println("=".repeat(80))

    } catch (e: Exception) {
        println()
        println("=".repeat(80))
        println("✗ CMA-ES optimization failed: ${e.message}")
        println("=".repeat(80))
        e.printStackTrace()
    }
}

/**
 * Run all optimization algorithms on a given training set with default parameters.
 * No hyperparameter optimization - just plain algorithms with standard settings.
 *
 * @param trainingDataPath Path to the training dataset CSV file
 * @param outputDir Directory where results will be saved
 * @param numSamples Number of samples to run for each algorithm
 * @param errorName Error metric to use (default: "DeltaE2000")
 * @param includeSparsityPenalty Whether to include sparsity penalty (default: true)
 * @param initialGuessType Type of initial guess (default: "Uniform")
 */
fun runAllAlgorithms(
    trainingDataPath: String = "C:\\Users\\safii\\IdeaProjects\\CI-Colors\\src\\main\\kotlin\\datasets\\training--3-colors-random-step.csv",
    outputDir: String = "C:\\Users\\safii\\IdeaProjects\\CI-Colors\\src\\main\\kotlin\\datasets\\results",
    numSamples: Int = 20,
    errorName: String = "DeltaE2000",
    includeSparsityPenalty: Boolean = true,
    initialGuessType: String = "Uniform"
) {
    val generator = SamplesGenerator()

    // List of all algorithms with their default parameters
    val algorithms = listOf(
        "CMA-ES" to mapOf(
            "populationMultiplier" to 10,
            "sigma" to 0.3,
            "diagonalOnly" to 10,
            "checkFeasibleCount" to 10,
            "stopFitness" to 0.001
        ),
        "NSGAII" to mapOf(
            "populationSize" to 175,
            "sbxRate" to 1.0,
            "sbxDistributionIndex" to 15.0,
            "pmRate" to 0.1,
            "pmDistributionIndex" to 20.0
        ),
//        "SPEA2" to mapOf(
//            "populationSize" to 100,
//            "archiveSize" to 100,
//            "sbxRate" to 1.0,
//            "sbxDistributionIndex" to 15.0,
//            "pmDistributionIndex" to 20.0
//        ),
        "SMSEMOA" to mapOf(
            "populationSize" to 100,
            "sbxRate" to 1.0,
            "sbxDistributionIndex" to 15.0,
            "pmDistributionIndex" to 20.0
        ),
        "BOBYQA" to mapOf(
            "maxEvaluations" to 10000,
            "numberOfInterpolationPoints" to 20
        ),
        "Powell" to mapOf(
            "maxEvaluations" to 50000
        ),
        "Nelder-Mead" to mapOf(
            "maxEvaluations" to 2000,
            "relativeThreshold" to 0.0001,
            "absoluteThreshold" to 0.00001,
            "stepSize" to 0.01
        )
    )

    println("=".repeat(80))
    println("Running all algorithms on training set")
    println("Training data: $trainingDataPath")
    println("Output directory: $outputDir")
    println("Number of samples per algorithm: $numSamples")
    println("=".repeat(80))
    println()

    for ((algorithmName, defaultParams) in algorithms) {
        val outputFile = "$outputDir\\results-$algorithmName-default.csv"

        println("Running $algorithmName...")
        println("  Parameters: $defaultParams")
        println("  Output: $outputFile")

        try {
            generator.generateOptimizationSamples(
                trainingDataPath = trainingDataPath,
                resultOutputPath = outputFile,
                optimizerName = algorithmName,
                errorName = errorName,
                includeSparsityPenalty = includeSparsityPenalty,
                initialGuessType = initialGuessType,
                optimizationParameters = defaultParams,
                numberOfSamples = numSamples
            )
            println("  ✓ Completed successfully")
        } catch (e: Exception) {
            println("  ✗ Failed: ${e.message}")
            e.printStackTrace()
        }

        println()
    }

    println("=".repeat(80))
    println("All algorithms completed!")
    println("Results saved to: $outputDir")
    println("=".repeat(80))
}

fun findColor() {
    val weights = "3.162157366057086E-4;0.010463127680289196;0.012118088726687798;0.011671387358361157;0.7776339453341855;0.017780668572952456;0.9815124396946103;0.0020047614125876453;0.01795195886660012;0.009614159150741573;0.009837374544195596".split(";")


    val w = weights.map { it.toDouble() }.toDoubleArray()

    val mixer = MixboxColorMixer()
    val color = mixer.mixColors(w, Palette.allColors)

    print(color)


    val recipe = Palette.allColors.zip(w.toList())
        .mapNotNull { (color, weight) ->
            if (weight > 0.1) color to weight else null
        }.toMap()

    for (color in recipe.keys) {
        println("${color.title}: ${recipe[color]}")
    }
}

fun rumOptimization(
    trainingDataPath: String,
    tempOutputDir: String,
    parameters: List<HyperparameterConfig>,
    optimizerName: String,
    numSamples: Int,
    useNelderMead: Boolean = false,
    maxEvaluations: Int = 1000,
    relativeThreshold: Double = 1e-6,
    absoluteThreshold: Double = 1e-6
    ) : Map<String, Any> {
    val optimizer = HyperparameterOptimizer(
        trainingDataPath = trainingDataPath,
        numSamples = numSamples,
        tempOutputDir = tempOutputDir,
        innerOptimizerName = optimizerName,
        hyperparameters = parameters
    )

    val best = if (useNelderMead) {
        println("===== Using Nelder-Mead for Hyperparameter Optimization =====")
        optimizer.optimizeWithNelderMead(maxEvaluations, relativeThreshold, absoluteThreshold)
    } else {
        println("===== Using CMA-ES for Hyperparameter Optimization =====")
        optimizer.optimize()
    }

    println("===== Best Hyperparameters =====")
    println("Optimizer: ${best.optimizerName}")
    println("Parameters:")
    best.parameters.forEach { (name, value) ->
        println("  $name = $value")
    }
    println("Mean Error: ${best.meanError}")

    return best.parameters
}

fun runNelderMeadHyperoptimization(){
    val trainingDataPath =
        "C:\\Users\\safii\\IdeaProjects\\CI-Colors\\src\\main\\kotlin\\datasets\\training--3-colors-random-step.csv"

    val tempOutputDir =
        "C:\\Users\\safii\\IdeaProjects\\CI-Colors\\src\\main\\kotlin\\datasets\\hyperparam_runs"

    val parameters = listOf(
        HyperparameterConfig(
            name = "maxEvaluations",
            initialValue = 2000.0,       // good default
            sigma = 800.0,               // exploration range
            lowerBound = 200.0,
            upperBound = 20000.0,
            transform = { it.toInt().coerceAtLeast(50) }
        ),
        HyperparameterConfig(
            name = "relativeThreshold",
            initialValue = -4.0,         // log10(1e-4)
            sigma = 1.0,                 // vary roughly 10x per step
            lowerBound = -8.0,           // log10(1e-8)
            upperBound = -1.0,           // log10(1e-1)
            transform = { 10.0.pow(it) } // exponentiate
        ),
        HyperparameterConfig(
            name = "absoluteThreshold",
            initialValue = -5.0,         // 1e-5
            sigma = 1.0,
            lowerBound = -10.0,          // 1e-10
            upperBound = -2.0,           // 1e-2
            transform = { 10.0.pow(it) }
        ),
        HyperparameterConfig(
            name = "stepSize",
            initialValue = -2.0,         // 10^-2 = 0.01
            sigma = 1.0,
            lowerBound = -4.0,           // 0.0001
            upperBound = 0.0,            // 1.0
            transform = { 10.0.pow(it) }
        )
    )

    rumOptimization(
        trainingDataPath = trainingDataPath,
        tempOutputDir = tempOutputDir,
        parameters = parameters,
        optimizerName = "Nelder-Mead",
        numSamples = 20,
        useNelderMead = true,        // Use Nelder-Mead as outer optimizer for speed
        maxEvaluations = 500,        // Fewer evaluations than CMA-ES
        relativeThreshold = 1e-4,    // Slightly relaxed for speed
        absoluteThreshold = 1e-4
    )
}

fun runCMAEHyperoptimization(){
    val trainingDataPath =
        "C:\\Users\\safii\\IdeaProjects\\CI-Colors\\src\\main\\kotlin\\datasets\\training--3-colors-random-step.csv"

    val tempOutputDir =
        "C:\\Users\\safii\\IdeaProjects\\CI-Colors\\src\\main\\kotlin\\datasets\\hyperparam_runs"

    // Define CMA-ES hyperparameters to optimize
    val cmaesHyperparams = listOf(
        HyperparameterConfig(
            name = "populationMultiplier",
            initialValue = 10.0,
            sigma = 5.0,
            lowerBound = 3.0,
            upperBound = 40.0,
            transform = { it.toInt() }
        ),
        HyperparameterConfig(
            name = "sigma",
            initialValue = 0.3,
            sigma = 0.1,
            lowerBound = 0.01,
            upperBound = 1.0,
            transform = { it }
        ),
        HyperparameterConfig(
            name = "diagonalOnly",
            initialValue = 10.0,
            sigma = 5.0,
            lowerBound = 0.0,
            upperBound = 20.0,
            transform = { it.toInt() }
        ),
        HyperparameterConfig(
            name = "checkFeasibleCount",
            initialValue = 10.0,
            sigma = 5.0,
            lowerBound = 0.0,
            upperBound = 20.0,
            transform = { it.toInt() }
        ),
        HyperparameterConfig(
            name = "stopFitness",
            initialValue = 1e-3,
            sigma = 1e-3,
            lowerBound = 1e-6,
            upperBound = 1e-2,
            transform = { it }
        )
    )

    rumOptimization(
        trainingDataPath = trainingDataPath,
        tempOutputDir = tempOutputDir,
        parameters = cmaesHyperparams,
        optimizerName = "CMA-ES",
        numSamples = 20
    )
}

// Example: Optimize NSGAII hyperparameters
fun runNSGAIIHyperoptimization() {
    val trainingDataPath =
        "C:\\Users\\safii\\IdeaProjects\\CI-Colors\\src\\main\\kotlin\\datasets\\training--3-colors-random-step.csv"

    val tempOutputDir =
        "C:\\Users\\safii\\IdeaProjects\\CI-Colors\\src\\main\\kotlin\\datasets\\hyperparam_runs"

    val nsgaiiHyperparams = listOf(
        HyperparameterConfig(
            name = "populationSize",
            initialValue = 175.0,
            sigma = 50.0,
            lowerBound = 50.0,
            upperBound = 500.0,
            transform = { it.toInt() }
        ),
        HyperparameterConfig(
            name = "sbx.rate",
            initialValue = 1.0,
            sigma = 0.5,
            lowerBound = 0.0,
            upperBound = 1.0,
            transform = { it.toInt() }
        ),
        HyperparameterConfig(
            name = "sbx.distributionIndex",
            initialValue = 15.0,
            sigma = 0.5,
            lowerBound = 0.0,
            upperBound = 30.0,
            transform = { it.toInt() }
        ),
        HyperparameterConfig(
            name = "pm.rate",
            initialValue = 0.1,
            sigma = 0.1,
            lowerBound = 0.0,
            upperBound = 0.2,
            transform = { it.toInt() }
        ),
        HyperparameterConfig(
            name = "pm.distributionIndex",
            initialValue = 20.0,
            sigma = 5.0,
            lowerBound = 0.0,
            upperBound = 40.0,
            transform = { it.toInt() }
        )
    )

    rumOptimization(
        trainingDataPath = trainingDataPath,
        tempOutputDir = tempOutputDir,
        parameters = nsgaiiHyperparams,
        optimizerName = "NSGAII",
        numSamples = 20
    )
}

// Optimize BOBYQA hyperparameters
fun runBOBYQAHyperoptimization() {
    val trainingDataPath =
        "C:\\Users\\safii\\IdeaProjects\\CI-Colors\\src\\main\\kotlin\\datasets\\training--3-colors-random-step.csv"

    val tempOutputDir =
        "C:\\Users\\safii\\IdeaProjects\\CI-Colors\\src\\main\\kotlin\\datasets\\hyperparam_runs"

    val bobyqaHyperparams = listOf(
        HyperparameterConfig(
            name = "maxEvaluations",
            initialValue = 10000.0,
            sigma = 5000.0,
            lowerBound = 1000.0,
            upperBound = 50000.0,
            transform = { it.toInt() }
        ),
        HyperparameterConfig(
            name = "numberOfInterpolationPoints",
            initialValue = 20.0,  // Typically 2*n+1 where n is dimension
            sigma = 10.0,
            lowerBound = 13.0,    // n+2 minimum (for 11 colors)
            upperBound = 50.0,    // Upper practical limit
            transform = { it.toInt() }
        )
    )

    rumOptimization(
        trainingDataPath = trainingDataPath,
        tempOutputDir = tempOutputDir,
        parameters = bobyqaHyperparams,
        optimizerName = "BOBYQA",
        numSamples = 20
    )
}

// Optimize Powell hyperparameters
fun runPowellHyperoptimization() {
    val trainingDataPath =
        "C:\\Users\\safii\\IdeaProjects\\CI-Colors\\src\\main\\kotlin\\datasets\\training--3-colors-random-step.csv"

    val tempOutputDir =
        "C:\\Users\\safii\\IdeaProjects\\CI-Colors\\src\\main\\kotlin\\datasets\\hyperparam_runs"

    val powellHyperparams = listOf(
        HyperparameterConfig(
            name = "maxEvaluations",
            initialValue = 50000.0,
            sigma = 25000.0,
            lowerBound = 5000.0,
            upperBound = 200000.0,
            transform = { it.toInt() }
        )
        // Note: Powell optimizer in the implementation uses fixed tolerance values (1e-6)
        // If you want to optimize those, you'd need to modify PowellOptimizerImpl constructor
    )

    rumOptimization(
        trainingDataPath = trainingDataPath,
        tempOutputDir = tempOutputDir,
        parameters = powellHyperparams,
        optimizerName = "Powell",
        numSamples = 20
    )
}

// Optimize SMSEMOA hyperparameters
fun runSMSEMOAHyperoptimization() {
    val trainingDataPath =
        "C:\\Users\\safii\\IdeaProjects\\CI-Colors\\src\\main\\kotlin\\datasets\\training--3-colors-random-step.csv"

    val tempOutputDir =
        "C:\\Users\\safii\\IdeaProjects\\CI-Colors\\src\\main\\kotlin\\datasets\\hyperparam_runs"

    val smsemoaHyperparams = listOf(
        HyperparameterConfig(
            name = "populationSize",
            initialValue = 100.0,
            sigma = 50.0,
            lowerBound = 50.0,
            upperBound = 500.0,
            transform = { it.toInt() }
        ),
        HyperparameterConfig(
            name = "sbxRate",
            initialValue = 1.0,
            sigma = 0.3,
            lowerBound = 0.5,
            upperBound = 1.0,
            transform = { it }
        ),
        HyperparameterConfig(
            name = "sbxDistributionIndex",
            initialValue = 15.0,
            sigma = 5.0,
            lowerBound = 5.0,
            upperBound = 30.0,
            transform = { it }
        ),
        HyperparameterConfig(
            name = "pmDistributionIndex",
            initialValue = 20.0,
            sigma = 5.0,
            lowerBound = 10.0,
            upperBound = 40.0,
            transform = { it }
        )
    )

    rumOptimization(
        trainingDataPath = trainingDataPath,
        tempOutputDir = tempOutputDir,
        parameters = smsemoaHyperparams,
        optimizerName = "SMSEMOA",
        numSamples = 20
    )
}

// Optimize SPEA2 hyperparameters
fun runSPEA2Hyperoptimization() {
    val trainingDataPath =
        "C:\\Users\\safii\\IdeaProjects\\CI-Colors\\src\\main\\kotlin\\datasets\\training--3-colors-random-step.csv"

    val tempOutputDir =
        "C:\\Users\\safii\\IdeaProjects\\CI-Colors\\src\\main\\kotlin\\datasets\\hyperparam_runs"

    val spea2Hyperparams = listOf(
        HyperparameterConfig(
            name = "populationSize",
            initialValue = 100.0,
            sigma = 50.0,
            lowerBound = 50.0,
            upperBound = 500.0,
            transform = { it.toInt() }
        ),
        HyperparameterConfig(
            name = "archiveSize",
            initialValue = 100.0,
            sigma = 50.0,
            lowerBound = 50.0,
            upperBound = 500.0,
            transform = { it.toInt() }
        ),
        HyperparameterConfig(
            name = "sbxRate",
            initialValue = 1.0,
            sigma = 0.3,
            lowerBound = 0.5,
            upperBound = 1.0,
            transform = { it }
        ),
        HyperparameterConfig(
            name = "sbxDistributionIndex",
            initialValue = 15.0,
            sigma = 5.0,
            lowerBound = 5.0,
            upperBound = 30.0,
            transform = { it }
        ),
        HyperparameterConfig(
            name = "pmDistributionIndex",
            initialValue = 20.0,
            sigma = 5.0,
            lowerBound = 10.0,
            upperBound = 40.0,
            transform = { it }
        )
    )

    rumOptimization(
        trainingDataPath = trainingDataPath,
        tempOutputDir = tempOutputDir,
        parameters = spea2Hyperparams,
        optimizerName = "SPEA2",
        numSamples = 20
    )
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


// ============================================================
// Fast Hyperparameter Optimization Functions Using Nelder-Mead
// ============================================================

/**
 * Fast CMA-ES hyperparameter optimization using Nelder-Mead as outer optimizer.
 * Significantly faster than using CMA-ES as outer optimizer.
 */
fun runCMAESHyperoptimizationFast() {
    val trainingDataPath =
        "C:\\Users\\safii\\IdeaProjects\\CI-Colors\\src\\main\\kotlin\\datasets\\training--3-colors-random-step.csv"

    val tempOutputDir =
        "C:\\Users\\safii\\IdeaProjects\\CI-Colors\\src\\main\\kotlin\\datasets\\hyperparam_runs"

    val cmaesHyperparams = listOf(
        HyperparameterConfig(
            name = "populationMultiplier",
            initialValue = 10.0,
            sigma = 5.0,
            lowerBound = 3.0,
            upperBound = 40.0,
            transform = { it.toInt() }
        ),
        HyperparameterConfig(
            name = "sigma",
            initialValue = 0.3,
            sigma = 0.1,
            lowerBound = 0.01,
            upperBound = 1.0,
            transform = { it }
        ),
        HyperparameterConfig(
            name = "diagonalOnly",
            initialValue = 10.0,
            sigma = 5.0,
            lowerBound = 0.0,
            upperBound = 20.0,
            transform = { it.toInt() }
        ),
        HyperparameterConfig(
            name = "checkFeasibleCount",
            initialValue = 10.0,
            sigma = 5.0,
            lowerBound = 0.0,
            upperBound = 20.0,
            transform = { it.toInt() }
        ),
        HyperparameterConfig(
            name = "stopFitness",
            initialValue = 1e-3,
            sigma = 1e-3,
            lowerBound = 1e-6,
            upperBound = 1e-2,
            transform = { it }
        )
    )

    rumOptimization(
        trainingDataPath = trainingDataPath,
        tempOutputDir = tempOutputDir,
        parameters = cmaesHyperparams,
        optimizerName = "CMA-ES",
        numSamples = 20,
        useNelderMead = true,
        maxEvaluations = 2000,
        relativeThreshold = 1e-4,
        absoluteThreshold = 1e-4
    )
}

/**
 * Fast BOBYQA hyperparameter optimization using Nelder-Mead as outer optimizer.
 */
fun runBOBYQAHyperoptimizationFast() {
    val trainingDataPath =
        "C:\\Users\\safii\\IdeaProjects\\CI-Colors\\src\\main\\kotlin\\datasets\\training--3-colors-random-step.csv"

    val tempOutputDir =
        "C:\\Users\\safii\\IdeaProjects\\CI-Colors\\src\\main\\kotlin\\datasets\\hyperparam_runs"

    val bobyqaHyperparams = listOf(
        HyperparameterConfig(
            name = "maxEvaluations",
            initialValue = 10000.0,
            sigma = 5000.0,
            lowerBound = 1000.0,
            upperBound = 50000.0,
            transform = { it.toInt() }
        ),
        HyperparameterConfig(
            name = "numberOfInterpolationPoints",
            initialValue = 20.0,
            sigma = 10.0,
            lowerBound = 13.0,
            upperBound = 50.0,
            transform = { it.toInt() }
        )
    )

    rumOptimization(
        trainingDataPath = trainingDataPath,
        tempOutputDir = tempOutputDir,
        parameters = bobyqaHyperparams,
        optimizerName = "BOBYQA",
        numSamples = 20,
        useNelderMead = true,
        maxEvaluations = 300,
        relativeThreshold = 1e-4,
        absoluteThreshold = 1e-4
    )
}

/**
 * Fast Powell hyperparameter optimization using Nelder-Mead as outer optimizer.
 */
fun runPowellHyperoptimizationFast() {
    val trainingDataPath =
        "C:\\Users\\safii\\IdeaProjects\\CI-Colors\\src\\main\\kotlin\\datasets\\training--3-colors-random-step.csv"

    val tempOutputDir =
        "C:\\Users\\safii\\IdeaProjects\\CI-Colors\\src\\main\\kotlin\\datasets\\hyperparam_runs"

    val powellHyperparams = listOf(
        HyperparameterConfig(
            name = "maxEvaluations",
            initialValue = 50000.0,
            sigma = 25000.0,
            lowerBound = 5000.0,
            upperBound = 200000.0,
            transform = { it.toInt() }
        )
    )

    rumOptimization(
        trainingDataPath = trainingDataPath,
        tempOutputDir = tempOutputDir,
        parameters = powellHyperparams,
        optimizerName = "Powell",
        numSamples = 20,
        useNelderMead = true,
        maxEvaluations = 200,
        relativeThreshold = 1e-4,
        absoluteThreshold = 1e-4
    )
}

/**
 * Fast NSGAII hyperparameter optimization using Nelder-Mead as outer optimizer.
 */
fun runNSGAIIHyperoptimizationFast() {
    val trainingDataPath =
        "C:\\Users\\safii\\IdeaProjects\\CI-Colors\\src\\main\\kotlin\\datasets\\training--3-colors-random-step.csv"

    val tempOutputDir =
        "C:\\Users\\safii\\IdeaProjects\\CI-Colors\\src\\main\\kotlin\\datasets\\hyperparam_runs"

    val nsgaiiHyperparams = listOf(
        HyperparameterConfig(
            name = "populationSize",
            initialValue = 175.0,
            sigma = 75.0,
            lowerBound = 50.0,
            upperBound = 500.0,
            transform = { it.toInt() }
        ),
        HyperparameterConfig(
            name = "sbxRate",
            initialValue = 1.0,
            sigma = 0.3,
            lowerBound = 0.0,
            upperBound = 1.0,
            transform = { it }
        ),
        HyperparameterConfig(
            name = "sbxDistributionIndex",
            initialValue = 15.0,
            sigma = 5.0,
            lowerBound = 0.0,
            upperBound = 30.0,
            transform = { it }
        ),
        HyperparameterConfig(
            name = "pmRate",
            initialValue = 0.1,
            sigma = 0.05,
            lowerBound = 0.0,
            upperBound = 0.2,
            transform = { it }
        ),
        HyperparameterConfig(
            name = "pmDistributionIndex",
            initialValue = 20.0,
            sigma = 5.0,
            lowerBound = 0.0,
            upperBound = 40.0,
            transform = { it }
        )
    )

    rumOptimization(
        trainingDataPath = trainingDataPath,
        tempOutputDir = tempOutputDir,
        parameters = nsgaiiHyperparams,
        optimizerName = "NSGAII",
        numSamples = 20,
        useNelderMead = true,
        maxEvaluations = 400,
        relativeThreshold = 1e-4,
        absoluteThreshold = 1e-4
    )
}

/**
 * Fast SMSEMOA hyperparameter optimization using Nelder-Mead as outer optimizer.
 */
fun runSMSEMOAHyperoptimizationFast() {
    val trainingDataPath =
        "C:\\Users\\safii\\IdeaProjects\\CI-Colors\\src\\main\\kotlin\\datasets\\training--3-colors-random-step.csv"

    val tempOutputDir =
        "C:\\Users\\safii\\IdeaProjects\\CI-Colors\\src\\main\\kotlin\\datasets\\hyperparam_runs"

    val smsemoaHyperparams = listOf(
        HyperparameterConfig(
            name = "populationSize",
            initialValue = 100.0,
            sigma = 50.0,
            lowerBound = 50.0,
            upperBound = 500.0,
            transform = { it.toInt() }
        ),
        HyperparameterConfig(
            name = "sbxRate",
            initialValue = 1.0,
            sigma = 0.3,
            lowerBound = 0.5,
            upperBound = 1.0,
            transform = { it }
        ),
        HyperparameterConfig(
            name = "sbxDistributionIndex",
            initialValue = 15.0,
            sigma = 5.0,
            lowerBound = 5.0,
            upperBound = 30.0,
            transform = { it }
        ),
        HyperparameterConfig(
            name = "pmDistributionIndex",
            initialValue = 20.0,
            sigma = 5.0,
            lowerBound = 10.0,
            upperBound = 40.0,
            transform = { it }
        )
    )

    rumOptimization(
        trainingDataPath = trainingDataPath,
        tempOutputDir = tempOutputDir,
        parameters = smsemoaHyperparams,
        optimizerName = "SMSEMOA",
        numSamples = 20,
        useNelderMead = true,
        maxEvaluations = 400,
        relativeThreshold = 1e-4,
        absoluteThreshold = 1e-4
    )
}

/**
 * Fast SPEA2 hyperparameter optimization using Nelder-Mead as outer optimizer.
 */
fun runSPEA2HyperoptimizationFast() {
    val trainingDataPath =
        "C:\\Users\\safii\\IdeaProjects\\CI-Colors\\src\\main\\kotlin\\datasets\\training--3-colors-random-step.csv"

    val tempOutputDir =
        "C:\\Users\\safii\\IdeaProjects\\CI-Colors\\src\\main\\kotlin\\datasets\\hyperparam_runs"

    val spea2Hyperparams = listOf(
        HyperparameterConfig(
            name = "populationSize",
            initialValue = 100.0,
            sigma = 50.0,
            lowerBound = 50.0,
            upperBound = 500.0,
            transform = { it.toInt() }
        ),
        HyperparameterConfig(
            name = "archiveSize",
            initialValue = 100.0,
            sigma = 50.0,
            lowerBound = 50.0,
            upperBound = 500.0,
            transform = { it.toInt() }
        ),
        HyperparameterConfig(
            name = "sbxRate",
            initialValue = 1.0,
            sigma = 0.3,
            lowerBound = 0.5,
            upperBound = 1.0,
            transform = { it }
        ),
        HyperparameterConfig(
            name = "sbxDistributionIndex",
            initialValue = 15.0,
            sigma = 5.0,
            lowerBound = 5.0,
            upperBound = 30.0,
            transform = { it }
        ),
        HyperparameterConfig(
            name = "pmDistributionIndex",
            initialValue = 20.0,
            sigma = 5.0,
            lowerBound = 10.0,
            upperBound = 40.0,
            transform = { it }
        )
    )

    rumOptimization(
        trainingDataPath = trainingDataPath,
        tempOutputDir = tempOutputDir,
        parameters = spea2Hyperparams,
        optimizerName = "SPEA2",
        numSamples = 20,
        useNelderMead = true,
        maxEvaluations = 2000,        // 5 parameters
        relativeThreshold = 1e-4,
        absoluteThreshold = 1e-4
    )
}

