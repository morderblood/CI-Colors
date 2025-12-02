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
    private val maxEvaluations: Int = 10000,
    private val relativeThreshold: Double = 1e-6,
    private val absoluteThreshold: Double = 1e-6,
    private val simplexStepSize: Double = 0.2
) : Optimizer {

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
        val simplex = NelderMeadSimplex(DoubleArray(dim) { simplexStepSize })

        // Create an optimizer with convergence criteria
        val optimizer = SimplexOptimizer(relativeThreshold, absoluteThreshold)

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

