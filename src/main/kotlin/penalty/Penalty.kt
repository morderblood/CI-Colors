package penalty

/**
 * Penalty Layer: Modifies objective value by adding costs for undesirable solutions.
 *
 * Penalties operate on numeric weights, never on domain objects.
 *
 * Examples:
 * - SparsityPenalty: penalize solutions with too many active colors
 * - SimilarityPenalty: penalize using similar colors together
 * - RegularizationPenalty: L1/L2 regularization
 *
 * Принцип: Штрафы работают только с числовыми весами, никогда с доменными объектами.
 */
interface Penalty {
    /**
     * Calculates the penalty value for a given weight vector.
     *
     * @param weights The normalized weight vector to evaluate
     * @return Penalty value (0 = no penalty, higher = worse)
     */
    fun calculate(weights: DoubleArray): Double
}
