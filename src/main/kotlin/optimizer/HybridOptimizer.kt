package optimizer

import goal.Goal

/**
 * Hybrid Optimizer: CMA-ES followed by Nelder-Mead refinement.
 *
 * This two-stage approach:
 * 1. CMA-ES: Global exploration to find promising region
 * 2. Nelder-Mead: Local refinement for precise solution
 *
 * Best of both worlds:
 * - Global search capability (avoid local minima)
 * - Fast local convergence
 *
 * Recommended for production use.
 */
class HybridOptimizer(
    optimizationParameters: Map<String, Any> = emptyMap(),
    private val optimizer1: Optimizer,
    private val optimizer2: Optimizer
) : Optimizer(optimizationParameters) {

    override fun optimize(
        goal: Goal,
        initialWeights: DoubleArray,
        bounds: Pair<DoubleArray, DoubleArray>?
    ): OptimizationResult {
        // Stage 1: Global optimization with the first optimizer (e.g., CMA-ES)
        val intermediateResult = optimizer1.optimize(goal, initialWeights, bounds)

        // Stage 2: Local refinement with the second optimizer (e.g., Nelder-Mead)
        val refinedResult = optimizer2.optimize(
            goal,
            intermediateResult.weights,
            bounds
        )

        val fullResults = refinedResult.copy(evaluations = refinedResult.evaluations + intermediateResult.evaluations)

        return fullResults
    }
}

