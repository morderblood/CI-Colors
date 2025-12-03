package optimizer

import goal.Goal
import org.apache.commons.math3.analysis.MultivariateFunction
import org.apache.commons.math3.optim.InitialGuess
import org.apache.commons.math3.optim.MaxEval
import org.apache.commons.math3.optim.SimpleBounds
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.PowellOptimizer

class PowellOptimizerImpl (
        private val maxEvaluations: Int = 10000
    ) : Optimizer {
    override fun optimize(
        goal: Goal,
        initialWeights: DoubleArray,
        bounds: Pair<DoubleArray, DoubleArray>?
    ): OptimizationResult {
        val dim = initialWeights.size
        val (lower, upper) = bounds ?: (DoubleArray(dim) { 0.0 } to DoubleArray(dim) { 1.0 })

        val optimizer = PowellOptimizer(1e-6, 1e-6)

        val result = optimizer.optimize(
            ObjectiveFunction(MultivariateFunction { goal.evaluate(it) }),
            GoalType.MINIMIZE,
            InitialGuess(initialWeights),
            MaxEval(maxEvaluations)
        )

        return OptimizationResult(
            weights = result.point,
            objectiveValue = result.value,
            evaluations = optimizer.evaluations,
            converged = true,
            algorithmName = "Powell"
        )
    }
}