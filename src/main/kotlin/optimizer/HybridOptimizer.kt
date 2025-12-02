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
    private val cmaesMaxEval: Int = 30000,
    private val nelderMeadMaxEval: Int = 10000,
    private val cmaesStopFitness: Double = 1e-6,
    private val refineThreshold: Double = 10.0
) : Optimizer {

    private val cmaes = CMAESOptimizerImpl(
        maxEvaluations = cmaesMaxEval,
        stopFitness = cmaesStopFitness
    )

    private val nelderMead = NelderMeadOptimizer(
        maxEvaluations = nelderMeadMaxEval
    )

    override fun optimize(
        goal: Goal,
        initialWeights: DoubleArray,
        bounds: Pair<DoubleArray, DoubleArray>?
    ): OptimizationResult {
        // Stage 1: Global search with CMA-ES
        val cmaesResult = cmaes.optimize(goal, initialWeights, bounds)

        // Check if the result is good enough to skip refinement
        if (cmaesResult.objectiveValue < cmaesStopFitness) {
            return cmaesResult.copy(
                algorithmName = "Hybrid (CMA-ES only, converged early)"
            )
        }

        // Stage 2: Local refinement with Nelder-Mead
        // Only refine if CMA-ES found a reasonable solution
        val refinedResult = if (cmaesResult.objectiveValue < refineThreshold) {
            try {
                nelderMead.optimize(goal, cmaesResult.weights, bounds)
            } catch (e: Exception) {
                // If Nelder-Mead fails, return CMA-ES result
                cmaesResult.copy(
                    algorithmName = "Hybrid (CMA-ES only, refinement failed)"
                )
            }
        } else {
            // CMA-ES result not good enough, return it anyway
            cmaesResult.copy(
                algorithmName = "Hybrid (CMA-ES only, poor convergence)"
            )
        }

        // Return best result
        return if (refinedResult.objectiveValue < cmaesResult.objectiveValue) {
            refinedResult.copy(
                evaluations = cmaesResult.evaluations + refinedResult.evaluations,
                algorithmName = "Hybrid (CMA-ES + Nelder-Mead)"
            )
        } else {
            cmaesResult.copy(
                algorithmName = "Hybrid (CMA-ES only, refinement didn't improve)"
            )
        }
    }
}

