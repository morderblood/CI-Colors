package penalty

/**
 * Penalty Implementation: Penalizes solutions with too many active pigments.
 *
 * Encourages sparse solutions (fewer colors used) which are often more practical
 * and easier to reproduce in real-world mixing scenarios.
 *
 * @property threshold Minimum weight for a color to be considered "active"
 * @property penaltyPerColor Penalty added for each active color
 */
class SparsityPenalty(
    private val threshold: Double = 0.01,
    private val penaltyPerColor: Double = 1.0
) : Penalty {

    override fun calculate(weights: DoubleArray): Double {
        val activeCount = weights.count { it > threshold }
        return activeCount * penaltyPerColor
    }
}