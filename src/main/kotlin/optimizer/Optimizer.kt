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
