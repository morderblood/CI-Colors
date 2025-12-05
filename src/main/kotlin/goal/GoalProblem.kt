package goal

import org.moeaframework.core.*
import org.moeaframework.core.objective.Objective
import org.moeaframework.core.variable.RealVariable
import org.moeaframework.problem.Problem

/**
 * Адаптер: оборачивает ваш Goal в MOEA Framework Problem.
 */
class GoalProblem(
    private val goal: Goal,
    private val dim: Int,
    private var lowerBounds: DoubleArray,
    private var upperBounds: DoubleArray
) : Problem {

    override fun getNumberOfVariables(): Int = dim
    override fun getNumberOfObjectives(): Int = 1
    override fun getNumberOfConstraints(): Int = 0

    override fun newSolution(): Solution {
        val solution = Solution(numberOfVariables, numberOfObjectives, numberOfConstraints)

        for (i in 0 until dim) {
            solution.setVariable(
                i,
                RealVariable(lowerBounds[i], upperBounds[i])
            )
        }
        return solution
    }

    override fun evaluate(solution: Solution) {
        val x = DoubleArray(dim) { i ->
            (solution.getVariable(i) as RealVariable).value
        }
        val value = goal.evaluate(x)
        val objective: Objective = Objective.createDefault().withValue(value)
        solution.setObjective(0, objective)
    }

    override fun close() {
        // no-op
    }
}
