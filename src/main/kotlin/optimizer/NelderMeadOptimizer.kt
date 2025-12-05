package optimizer

import goal.Goal
import org.apache.commons.math3.analysis.MultivariateFunction
import org.apache.commons.math3.optim.InitialGuess
import org.apache.commons.math3.optim.MaxEval
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.NelderMeadSimplex
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.SimplexOptimizer

/**
 * Nelder-Mead Simplex Optimizer.
 *
 * A local optimization algorithm that's:
 * - Derivative-free
 * - Good for refinement after global search
 * - Relatively fast for low-dimensional problems
 * - Simple and robust
 *
 * Best used after CMA-ES for local refinement.
 */
class NelderMeadOptimizer(
    optimizationParameters: Map<String, Any> = emptyMap()
) : Optimizer(optimizationParameters) {

    override fun optimize(
        goal: Goal,
        initialWeights: DoubleArray,
        bounds: Pair<DoubleArray, DoubleArray>?
    ): OptimizationResult {
        val dim = initialWeights.size

        // Wrap Goal in MultivariateFunction
        val objectiveFunction = MultivariateFunction { point ->
            goal.evaluate(point)
        }

        // Create simplex with specified step size
        val simplex = NelderMeadSimplex(DoubleArray(dim) { optimizationParameters.getOrDefault("stepSize", 10) as Double })

        // Create an optimizer with convergence criteria
        val optimizer = SimplexOptimizer(
            optimizationParameters.getOrDefault("relativeThreshold", 1e-6) as Double,
            optimizationParameters.getOrDefault("absoluteThreshold", 1e-6) as Double)

        val maxEvaluations = optimizationParameters.getOrDefault("maxEvaluations", 100000) as Int

        // Run optimization
        val result = optimizer.optimize(
            ObjectiveFunction(objectiveFunction),
            GoalType.MINIMIZE,
            InitialGuess(initialWeights),
            simplex,
            MaxEval(maxEvaluations)
        )

        // Return standardized result
        return OptimizationResult(
            weights = result.point,
            objectiveValue = result.value,
            evaluations = optimizer.evaluations,
            converged = optimizer.evaluations < maxEvaluations,
            algorithmName = "Nelder-Mead"
        )
    }
}

