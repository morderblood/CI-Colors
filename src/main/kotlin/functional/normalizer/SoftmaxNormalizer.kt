package functional.normalizer

import kotlin.math.exp

/**
 * Normalizer Implementation: Softmax normalization.
 *
 * Uses exponential function to convert weights to probabilities.
 * Useful when you want to:
 * - Maintain relative ordering of weights
 * - Amplify differences between large and small weights
 * - Ensure smooth gradients for gradient-based optimizers
 *
 * Formula: weight[i] = exp(raw[i]) / sum(exp(raw[j]))
 */
class SoftmaxNormalizer : Normalizer {

    override fun normalize(weights: DoubleArray): DoubleArray {
        if (weights.isEmpty()) {
            return doubleArrayOf()
        }

        // Find max for numerical stability (prevent overflow)
        val maxWeight = weights.maxOrNull() ?: 0.0

        // Calculate exp(w - max) for each weight
        val expWeights = weights.map { exp(it - maxWeight) }
        val sumExp = expWeights.sum()

        return if (sumExp > 0.0) {
            expWeights.map { it / sumExp }.toDoubleArray()
        } else {
            // Fallback: uniform distribution
            DoubleArray(weights.size) { 1.0 / weights.size }
        }
    }
}

