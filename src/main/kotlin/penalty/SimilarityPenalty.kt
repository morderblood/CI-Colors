package penalty

/**
 * Penalty Implementation: Penalizes simultaneous use of similar colors.
 *
 * Prevents redundant color combinations (e.g., using both "Vermilion Red" and
 * "Rouge Carmine Red" when they are very similar).
 *
 * @property similarityPairs List of index pairs that should not be used together
 * @property threshold Minimum weight for a color to be considered "active"
 * @property penaltyPerPair Penalty added for each violated pair
 */
class SimilarityPenalty(
    private val similarityPairs: List<Pair<Int, Int>>,
    private val threshold: Double = 0.1,
    private val penaltyPerPair: Double = 1.0
) : Penalty {

    override fun calculate(weights: DoubleArray): Double {
        var penalty = 0.0
        for ((i, j) in similarityPairs) {
            // Check bounds to prevent index out of bounds
            if (i < weights.size && j < weights.size) {
                if (weights[i] > threshold && weights[j] > threshold) {
                    penalty += penaltyPerPair
                }
            }
        }
        return penalty
    }
}