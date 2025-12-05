package optimizer

import goal.Goal
import goal.GoalProblem
import org.moeaframework.core.TypedProperties
import org.moeaframework.core.population.NondominatedPopulation
import org.moeaframework.core.variable.RealVariable
import org.moeaframework.core.spi.AlgorithmFactory

/**
 * Пример Optimizer на базе MOEA (NSGA-II) для одной цели.
 */
abstract class MOEAOptimizerImp(
    private val maxEvaluations: Int = 10000,
    private val populationSize: Int = 20
) : Optimizer {

    abstract val algorithmName: String
    abstract val properties: TypedProperties

    override fun optimize(
        goal: Goal,
        initialWeights: DoubleArray,
        bounds: Pair<DoubleArray, DoubleArray>?
    ): OptimizationResult {

        val dim = initialWeights.size
        val lowerBounds = bounds?.first ?: DoubleArray(dim) { 0.0 }
        val upperBounds = bounds?.second ?: DoubleArray(dim) { 1.0 }

        val problem = GoalProblem(goal, dim, lowerBounds, upperBounds)

        // Create algorithm
        val algorithm = AlgorithmFactory.getInstance().getAlgorithm(
            algorithmName,
            properties,
            problem
        )

        var evaluations = 0
        while (!algorithm.isTerminated && evaluations < maxEvaluations) {
            algorithm.step()
            evaluations++
        }

        val resultPop: NondominatedPopulation = algorithm.result
        algorithm.terminate()

        val best = resultPop.minByOrNull { it.getObjective(0) }
            ?: throw IllegalStateException("No solution found")

        val bestWeights = DoubleArray(dim) { i ->
            (best.getVariable(i) as RealVariable).value
        }

        return OptimizationResult(
            weights = bestWeights,
            objectiveValue = best.getObjective(0).value,
            evaluations = evaluations,
            converged = true,
            algorithmName = algorithmName
        )
    }
}
