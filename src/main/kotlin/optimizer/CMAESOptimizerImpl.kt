package optimizer

import goal.Goal
import org.apache.commons.math3.analysis.MultivariateFunction
import org.apache.commons.math3.optim.InitialGuess
import org.apache.commons.math3.optim.MaxEval
import org.apache.commons.math3.optim.SimpleBounds
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.CMAESOptimizer
import org.apache.commons.math3.random.MersenneTwister

/**
 * CMA-ES (Covariance Matrix Adaptation Evolution Strategy) Optimizer.
 *
 * A global optimization algorithm that's particularly effective for:
 * - Non-convex optimization
 * - Noisy objective functions
 * - High-dimensional problems
 *
 * Good for initial exploration before local refinement.
 */
class CMAESOptimizerImpl(
    optimizationParameters: Map<String, Any> = emptyMap()
) : Optimizer(optimizationParameters) {

    override fun optimize(
        goal: Goal,
        initialWeights: DoubleArray,
        bounds: Pair<DoubleArray, DoubleArray>?
    ): OptimizationResult {
        val dim = initialWeights.size

        // Wrap Goal in MultivariateFunction for Apache Commons Math
        val objectiveFunction = MultivariateFunction { point ->
            goal.evaluate(point)
        }

        // Use provided bounds or default to [0.0, 1.0] for each dimension
        val lowerBounds = bounds?.first ?: DoubleArray(dim) { 0.0 }
        val upperBounds = bounds?.second ?: DoubleArray(dim) { 1.0 }

        val maxEvaluations = optimizationParameters.getOrDefault("maxEvaluations", 10000) as Int
        val stopFitness = optimizationParameters.getOrDefault("stopFitness", 1e-3) as Double

        // Create CMA-ES optimizer
        val optimizer = CMAESOptimizer(
            maxEvaluations,
            stopFitness,
            true,  // isActiveCMA
            optimizationParameters.getOrDefault("diagonalOnly", 10) as Int,     // diagonalOnly
            0,     // checkFeasibleCount
            MersenneTwister(),
            false, // generateStatistics
            null   // convergenceChecker
        )

        // Run optimization
        val result = optimizer.optimize(
            MaxEval(maxEvaluations),
            ObjectiveFunction(objectiveFunction),
            GoalType.MINIMIZE,
            InitialGuess(initialWeights),
            SimpleBounds(lowerBounds, upperBounds),
            CMAESOptimizer.Sigma(DoubleArray(dim) {
                optimizationParameters.getOrDefault("sigma", 0.3) as Double
            }),
            CMAESOptimizer.PopulationSize(
                optimizationParameters.getOrDefault("populationMultiplier", 5) as Int * dim)
        )

        // Return our standardized result
        return OptimizationResult(
            weights = result.point,
            objectiveValue = result.value,
            evaluations = optimizer.evaluations,
            converged = result.value < stopFitness,
            algorithmName = "CMA-ES"
        )
    }
}