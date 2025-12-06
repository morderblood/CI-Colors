package hyperparameter

import org.apache.commons.math3.analysis.MultivariateFunction
import org.apache.commons.math3.optim.InitialGuess
import org.apache.commons.math3.optim.MaxEval
import org.apache.commons.math3.optim.SimpleBounds
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.CMAESOptimizer
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.NelderMeadSimplex
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.SimplexOptimizer
import org.apache.commons.math3.random.MersenneTwister

/**
 * Generic hyperparameter optimizer using CMA-ES as the outer optimizer.
 * Can optimize hyperparameters for any inner optimizer.
 *
 * @param trainingDataPath Path to training dataset
 * @param numSamples Number of samples to evaluate per hyperparameter configuration
 * @param tempOutputDir Directory for temporary output files
 * @param innerOptimizerName Name of the optimizer to optimize hyperparameters for
 * @param hyperparameters List of hyperparameter configurations to optimize
 */
class HyperparameterOptimizer(
    private val trainingDataPath: String,
    private val numSamples: Int,
    private val tempOutputDir: String,
    private val innerOptimizerName: String,
    private val hyperparameters: List<HyperparameterConfig>
) {

    /**
     * Run hyperparameter optimization.
     *
     * @return Best hyperparameter configuration found
     */
    fun optimize(): HyperparameterSample {

        val objectiveWrapper = HyperparameterObjective(
            trainingDataPath,
            numSamples,
            tempOutputDir,
            innerOptimizerName,
            hyperparameters
        )

        // Convert to Apache Commons Math interface
        val objective = MultivariateFunction { x ->
            objectiveWrapper.evaluate(x)
        }

        val initial = hyperparameters.map { it.initialValue }.toDoubleArray()
        val sigma = hyperparameters.map { it.sigma }.toDoubleArray()
        val lower = hyperparameters.map { it.lowerBound }.toDoubleArray()
        val upper = hyperparameters.map { it.upperBound }.toDoubleArray()

        val optimizer = CMAESOptimizer(
            300,
            0.0,
            true,
            5,
            0,
            MersenneTwister(),
            false,
            null
        )

        val result = optimizer.optimize(
            MaxEval(300),
            ObjectiveFunction(objective),
            GoalType.MINIMIZE,
            InitialGuess(initial),
            SimpleBounds(lower, upper),
            CMAESOptimizer.Sigma(sigma),
            CMAESOptimizer.PopulationSize(20)
        )

        val x = result.point

        // Transform optimized values back to actual parameters
        val paramMap = hyperparameters.mapIndexed { i, config ->
            config.name to config.transform(x[i])
        }.toMap()

        return HyperparameterSample(
            optimizerName = innerOptimizerName,
            parameters = paramMap,
            meanError = result.value
        )
    }

    /**
     * Run hyperparameter optimization using Nelder-Mead simplex algorithm.
     * This is typically faster than CMA-ES but may be less robust for complex landscapes.
     *
     * Note: Nelder-Mead does not natively support bounds constraints. This implementation
     * uses a penalty method where out-of-bounds evaluations return a high penalty value.
     * Final results are clamped to bounds.
     *
     * @param maxEvaluations Maximum number of function evaluations (default: 1000)
     * @param relativeThreshold Relative convergence threshold (default: 1e-6)
     * @param absoluteThreshold Absolute convergence threshold (default: 1e-6)
     * @return Best hyperparameter configuration found
     */
    fun optimizeWithNelderMead(
        maxEvaluations: Int = 1000,
        relativeThreshold: Double = 1e-6,
        absoluteThreshold: Double = 1e-6
    ): HyperparameterSample {

        val objectiveWrapper = HyperparameterObjective(
            trainingDataPath,
            numSamples,
            tempOutputDir,
            innerOptimizerName,
            hyperparameters
        )

        // Wrap objective with bounds checking (penalty method)
        // Nelder-Mead doesn't support SimpleBounds, so we use penalties
        val objective = MultivariateFunction { x ->
            // Check bounds and apply penalty if violated
            val outOfBounds = hyperparameters.indices.any { i ->
                x[i] < hyperparameters[i].lowerBound || x[i] > hyperparameters[i].upperBound
            }

            if (outOfBounds) {
                // Return a high penalty value
                Double.MAX_VALUE / 2.0
            } else {
                objectiveWrapper.evaluate(x)
            }
        }

        val initial = hyperparameters.map { it.initialValue }.toDoubleArray()

        // Create step sizes for simplex (10% of the range)
        val stepSizes = hyperparameters.map { config ->
            (config.upperBound - config.lowerBound) * 0.1
        }.toDoubleArray()

        // Create Nelder-Mead optimizer with convergence thresholds
        val optimizer = SimplexOptimizer(relativeThreshold, absoluteThreshold)

        // Create the simplex with initial point and step sizes
        val simplex = NelderMeadSimplex(stepSizes)

        // Note: SimplexOptimizer does NOT support SimpleBounds
        val result = optimizer.optimize(
            MaxEval(maxEvaluations),
            ObjectiveFunction(objective),
            GoalType.MINIMIZE,
            InitialGuess(initial),
            simplex
        )

        val x = result.point

        // Transform optimized values back to actual parameters
        // Clamp to bounds in case they drifted slightly outside
        val paramMap = hyperparameters.mapIndexed { i, config ->
            val clampedValue = x[i].coerceIn(config.lowerBound, config.upperBound)
            config.name to config.transform(clampedValue)
        }.toMap()

        return HyperparameterSample(
            optimizerName = innerOptimizerName,
            parameters = paramMap,
            meanError = result.value
        )
    }
}
