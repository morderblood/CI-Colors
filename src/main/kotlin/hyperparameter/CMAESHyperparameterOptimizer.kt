package hyperparameter

import org.apache.commons.math3.analysis.MultivariateFunction
import org.apache.commons.math3.optim.InitialGuess
import org.apache.commons.math3.optim.MaxEval
import org.apache.commons.math3.optim.SimpleBounds
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.CMAESOptimizer
import org.apache.commons.math3.random.MersenneTwister

class CMAESHyperparameterOptimizer(
    private val trainingDataPath: String,
    private val numSamples: Int,
    private val tempOutputDir: String
) {

    fun optimize(): HyperparameterSample {

        val objectiveWrapper = CMAESHyperparameterObjective(
            trainingDataPath,
            numSamples,
            tempOutputDir
        )

        // Convert Objective interface for Apache CMA-ES
        val objective = MultivariateFunction { x ->
            objectiveWrapper.evaluate(x)
        }

        val dim = 5
        val initial = doubleArrayOf(10.0, 0.3, 10.0, 10.0, 1e-3)
        val sigma   = doubleArrayOf(5.0, 0.1, 5.0, 5.0, 1e-3)

        val lower = doubleArrayOf( 3.0, 0.01, 0.0, 0.0, 1e-6)
        val upper = doubleArrayOf(40.0, 1.00, 20.0, 20.0, 1e-2)

        val optimizer = CMAESOptimizer(
            300,
            0.0,
            true,
            5,
            0,
            MersenneTwister(),
            false,
            null
        )

        val result = optimizer.optimize(
            MaxEval(300),
            ObjectiveFunction(objective),
            GoalType.MINIMIZE,
            InitialGuess(initial),
            SimpleBounds(lower, upper),
            CMAESOptimizer.Sigma(sigma),
            CMAESOptimizer.PopulationSize(20)
        )

        val x = result.point

        return HyperparameterSample(
            populationMultiplier = x[0].toInt(),
            sigma = x[1],
            diagonalOnly = x[2].toInt(),
            checkFeasibleCount = x[3].toInt(),
            stopFitness = x[4],
            meanError = result.value
        )
    }
}
