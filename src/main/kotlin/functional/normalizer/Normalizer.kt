package functional.normalizer

/**
 * Functional Layer: Transforms raw weights into normalized proportions.
 *
 * Pure function: no side effects, deterministic, stable.
 *
 * Принцип: Функциональные компоненты должны быть чистыми функциями.
 */
interface Normalizer {
    /**
     * Normalizes raw weights into valid proportions.
     *
     * @param weights Raw weight vector (may contain negative or unnormalized values)
     * @return Normalized weights (non-negative, sum to 1.0)
     */
    fun normalize(weights: DoubleArray): DoubleArray
}
