package optimizer

import goal.Goal
import org.apache.commons.math3.optim.InitialGuess
import org.apache.commons.math3.optim.MaxEval
import org.apache.commons.math3.optim.SimpleBounds
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.BOBYQAOptimizer

class BOBYQAOptimizerImpl(
    optimizationParameters: Map<String, Any> = emptyMap()
) : Optimizer(optimizationParameters) {
    override fun optimize(
        goal: Goal,
        initialWeights: DoubleArray,
        bounds: Pair<DoubleArray, DoubleArray>?
    ): OptimizationResult {
        val dim = initialWeights.size
        val (lower, upper) = bounds ?: (DoubleArray(dim) { 0.0 } to DoubleArray(dim) { 1.0 })

        val optimizer = BOBYQAOptimizer(
            optimizationParameters["numberOfInterpolationPoints"] as? Int ?: (2 * dim + 1),
        )

        val result = optimizer.optimize(
            ObjectiveFunction { goal.evaluate(it) },
            GoalType.MINIMIZE,
            InitialGuess(initialWeights),
            SimpleBounds(lower, upper),
            MaxEval(
                optimizationParameters.getOrDefault("maxEvaluations", 10000) as Int
            )
        )

        return OptimizationResult(
            weights = result.point,
            objectiveValue = result.value,
            evaluations = optimizer.evaluations,
            converged = true,
            algorithmName = "BOBYQA"
        )
    }
}
