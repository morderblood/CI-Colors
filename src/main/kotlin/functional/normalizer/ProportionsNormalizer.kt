package functional.normalizer

/**
 * Functional Layer Implementation: Sum-to-1 normalization strategy.
 *
 * Ensures:
 * 1. All weights are non-negative (clips negative values to 0)
 * 2. Sum of weights equals 1.0
 * 3. Handles edge case when all weights are zero (returns uniform distribution)
 *
 * Pure function with no side effects.
 */
class ProportionsNormalizer : Normalizer {
    override fun normalize(weights: DoubleArray): DoubleArray {
        // Step 1: Clip negative values to 0
        val clipped = weights.map { if (it < 0.0) 0.0 else it }

        // Step 2: Calculate sum
        val sum = clipped.sum()

        // Step 3: Normalize or return uniform distribution
        return if (sum == 0.0) {
            // Edge case: all weights are zero → uniform distribution
            DoubleArray(weights.size) { 1.0 / weights.size }
        } else {
            // Normal case: divide by sum
            clipped.map { it / sum }.toDoubleArray()
        }
    }
}