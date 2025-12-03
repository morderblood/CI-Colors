package optimizer

/**
 * Result of an optimization run.
 *
 * @property weights Optimized weight vector
 * @property objectiveValue Final objective function value
 * @property evaluations Number of function evaluations performed
 * @property converged Whether the optimizer converged successfully
 * @property algorithmName Name of the algorithm used
 */
data class OptimizationResult(
    val weights: DoubleArray,
    val objectiveValue: Double,
    val evaluations: Int,
    val converged: Boolean,
    val algorithmName: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is OptimizationResult) return false
        if (!weights.contentEquals(other.weights)) return false
        if (objectiveValue != other.objectiveValue) return false
        if (evaluations != other.evaluations) return false
        if (converged != other.converged) return false
        if (algorithmName != other.algorithmName) return false
        return true
    }

    override fun hashCode(): Int {
        var result = weights.contentHashCode()
        result = 31 * result + objectiveValue.hashCode()
        result = 31 * result + evaluations
        result = 31 * result + converged.hashCode()
        result = 31 * result + algorithmName.hashCode()
        return result
    }
}