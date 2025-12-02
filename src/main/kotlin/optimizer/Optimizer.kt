package optimizer

import goal.Goal

/**
 * Optimizer Layer: Executes optimization according to a chosen algorithm.
 *
 * Optimizers operate only on Goals and weight vectors, not on colors or penalties directly.
 *
 * Expected implementations:
 * - Local optimizers (Nelder-Mead, Powell)
 * - Global optimizers (CMA-ES, PSO, GA)
 * - Hybrid optimizers (CMA-ES → Nelder-Mead refinement)
 *
 * Принцип: Оптимизаторы работают только с Goal и векторами весов.
 */
interface Optimizer {
    /**
     * Optimizes the goal function starting from an initial guess.
     *
     * @param goal The objective function to minimize
     * @param initialWeights Starting point for optimization
     * @param bounds Optional bounds for each weight (min, max)
     * @return Optimization result containing final weights and objective value
     */
    fun optimize(
        goal: Goal,
        initialWeights: DoubleArray,
        bounds: Pair<DoubleArray, DoubleArray>? = null
    ): OptimizationResult
}

/**
 * Result of an optimization run.
 *
 * @property weights Optimized weight vector
 * @property objectiveValue Final objective function value
 * @property evaluations Number of function evaluations performed
 * @property converged Whether the optimizer converged successfully
 * @property algorithmName Name of the algorithm used
 */
data class OptimizationResult(
    val weights: DoubleArray,
    val objectiveValue: Double,
    val evaluations: Int,
    val converged: Boolean,
    val algorithmName: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is OptimizationResult) return false
        if (!weights.contentEquals(other.weights)) return false
        if (objectiveValue != other.objectiveValue) return false
        if (evaluations != other.evaluations) return false
        if (converged != other.converged) return false
        if (algorithmName != other.algorithmName) return false
        return true
    }

    override fun hashCode(): Int {
        var result = weights.contentHashCode()
        result = 31 * result + objectiveValue.hashCode()
        result = 31 * result + evaluations
        result = 31 * result + converged.hashCode()
        result = 31 * result + algorithmName.hashCode()
        return result
    }
}
