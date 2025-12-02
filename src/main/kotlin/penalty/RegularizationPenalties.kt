package penalty

import kotlin.math.abs

/**
 * Penalty Implementation: L1 regularization (Lasso).
 *
 * Adds penalty proportional to the sum of absolute values of weights.
 * Encourages sparse solutions by pushing small weights toward zero.
 *
 * Formula: penalty = lambda * sum(|weight[i]|)
 *
 * @property lambda Regularization strength
 */
class L1RegularizationPenalty(
    private val lambda: Double = 0.1
) : Penalty {

    override fun calculate(weights: DoubleArray): Double {
        return lambda * weights.sumOf { abs(it) }
    }
}

/**
 * Penalty Implementation: L2 regularization (Ridge).
 *
 * Adds penalty proportional to the sum of squared weights.
 * Discourages large weights and promotes smoother solutions.
 *
 * Formula: penalty = lambda * sum(weight[i]^2)
 *
 * @property lambda Regularization strength
 */
class L2RegularizationPenalty(
    private val lambda: Double = 0.1
) : Penalty {

    override fun calculate(weights: DoubleArray): Double {
        return lambda * weights.sumOf { it * it }
    }
}

