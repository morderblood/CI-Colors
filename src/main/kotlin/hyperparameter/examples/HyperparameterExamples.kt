package hyperparameter.examples

import hyperparameter.HyperparameterConfig
import hyperparameter.HyperparameterOptimizer

/**
 * Examples of hyperparameter optimization for different optimizers.
 */
object HyperparameterExamples {

    /**
     * Optimize CMA-ES hyperparameters.
     */
    fun optimizeCMAES(trainingDataPath: String, tempOutputDir: String) {
        val hyperparams = listOf(
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

        val optimizer = HyperparameterOptimizer(
            trainingDataPath = trainingDataPath,
            numSamples = 20,
            tempOutputDir = tempOutputDir,
            innerOptimizerName = "CMA-ES",
            hyperparameters = hyperparams
        )

        val best = optimizer.optimize()
        printResults("CMA-ES", best)
    }

    /**
     * Optimize NSGAII hyperparameters.
     */
    fun optimizeNSGAII(trainingDataPath: String, tempOutputDir: String) {
        val hyperparams = listOf(
            HyperparameterConfig(
                name = "populationSize",
                initialValue = 100.0,
                sigma = 50.0,
                lowerBound = 50.0,
                upperBound = 500.0,
                transform = { it.toInt() }
            ),
            HyperparameterConfig(
                name = "maxGenerations",
                initialValue = 1000.0,
                sigma = 500.0,
                lowerBound = 100.0,
                upperBound = 5000.0,
                transform = { it.toInt() }
            )
        )

        val optimizer = HyperparameterOptimizer(
            trainingDataPath = trainingDataPath,
            numSamples = 20,
            tempOutputDir = tempOutputDir,
            innerOptimizerName = "NSGAII",
            hyperparameters = hyperparams
        )

        val best = optimizer.optimize()
        printResults("NSGAII", best)
    }

    /**
     * Optimize SPEA2 hyperparameters.
     */
    fun optimizeSPEA2(trainingDataPath: String, tempOutputDir: String) {
        val hyperparams = listOf(
            HyperparameterConfig(
                name = "populationSize",
                initialValue = 100.0,
                sigma = 50.0,
                lowerBound = 50.0,
                upperBound = 500.0,
                transform = { it.toInt() }
            ),
            HyperparameterConfig(
                name = "maxGenerations",
                initialValue = 1000.0,
                sigma = 500.0,
                lowerBound = 100.0,
                upperBound = 5000.0,
                transform = { it.toInt() }
            )
        )

        val optimizer = HyperparameterOptimizer(
            trainingDataPath = trainingDataPath,
            numSamples = 20,
            tempOutputDir = tempOutputDir,
            innerOptimizerName = "SPEA2",
            hyperparameters = hyperparams
        )

        val best = optimizer.optimize()
        printResults("SPEA2", best)
    }

    /**
     * Optimize BOBYQA hyperparameters.
     */
    fun optimizeBOBYQA(trainingDataPath: String, tempOutputDir: String) {
        val hyperparams = listOf(
            HyperparameterConfig(
                name = "maxEvaluations",
                initialValue = 1000.0,
                sigma = 500.0,
                lowerBound = 100.0,
                upperBound = 10000.0,
                transform = { it.toInt() }
            ),
            HyperparameterConfig(
                name = "initialTrustRegionRadius",
                initialValue = 0.5,
                sigma = 0.2,
                lowerBound = 0.1,
                upperBound = 2.0,
                transform = { it }
            ),
            HyperparameterConfig(
                name = "stoppingTrustRegionRadius",
                initialValue = 1e-6,
                sigma = 1e-6,
                lowerBound = 1e-10,
                upperBound = 1e-3,
                transform = { it }
            )
        )

        val optimizer = HyperparameterOptimizer(
            trainingDataPath = trainingDataPath,
            numSamples = 20,
            tempOutputDir = tempOutputDir,
            innerOptimizerName = "BOBYQA",
            hyperparameters = hyperparams
        )

        val best = optimizer.optimize()
        printResults("BOBYQA", best)
    }

    /**
     * Optimize Powell hyperparameters.
     */
    fun optimizePowell(trainingDataPath: String, tempOutputDir: String) {
        val hyperparams = listOf(
            HyperparameterConfig(
                name = "maxEvaluations",
                initialValue = 1000.0,
                sigma = 500.0,
                lowerBound = 100.0,
                upperBound = 10000.0,
                transform = { it.toInt() }
            ),
            HyperparameterConfig(
                name = "relativeTolerance",
                initialValue = 1e-6,
                sigma = 1e-6,
                lowerBound = 1e-10,
                upperBound = 1e-3,
                transform = { it }
            ),
            HyperparameterConfig(
                name = "absoluteTolerance",
                initialValue = 1e-8,
                sigma = 1e-8,
                lowerBound = 1e-12,
                upperBound = 1e-4,
                transform = { it }
            )
        )

        val optimizer = HyperparameterOptimizer(
            trainingDataPath = trainingDataPath,
            numSamples = 20,
            tempOutputDir = tempOutputDir,
            innerOptimizerName = "Powell",
            hyperparameters = hyperparams
        )

        val best = optimizer.optimize()
        printResults("Powell", best)
    }

    /**
     * Optimize Nelder-Mead hyperparameters.
     */
    fun optimizeNelderMead(trainingDataPath: String, tempOutputDir: String) {
        val hyperparams = listOf(
            HyperparameterConfig(
                name = "maxEvaluations",
                initialValue = 1000.0,
                sigma = 500.0,
                lowerBound = 100.0,
                upperBound = 10000.0,
                transform = { it.toInt() }
            ),
            HyperparameterConfig(
                name = "rho",
                initialValue = 1.0,
                sigma = 0.5,
                lowerBound = 0.5,
                upperBound = 2.0,
                transform = { it }
            ),
            HyperparameterConfig(
                name = "chi",
                initialValue = 2.0,
                sigma = 0.5,
                lowerBound = 1.0,
                upperBound = 3.0,
                transform = { it }
            ),
            HyperparameterConfig(
                name = "gamma",
                initialValue = 0.5,
                sigma = 0.2,
                lowerBound = 0.1,
                upperBound = 0.9,
                transform = { it }
            ),
            HyperparameterConfig(
                name = "sigma",
                initialValue = 0.5,
                sigma = 0.2,
                lowerBound = 0.1,
                upperBound = 0.9,
                transform = { it }
            )
        )

        val optimizer = HyperparameterOptimizer(
            trainingDataPath = trainingDataPath,
            numSamples = 20,
            tempOutputDir = tempOutputDir,
            innerOptimizerName = "Nelder-Mead",
            hyperparameters = hyperparams
        )

        val best = optimizer.optimize()
        printResults("Nelder-Mead", best)
    }

    /**
     * Helper to print optimization results.
     */
    private fun printResults(optimizerName: String, best: hyperparameter.HyperparameterSample) {
        println("===== Best $optimizerName Hyperparameters =====")
        println("Optimizer: ${best.optimizerName}")
        println("Parameters:")
        best.parameters.forEach { (name, value) ->
            println("  $name = $value")
        }
        println("Mean Error: ${best.meanError}")
        println()
    }
}

