package state

import domain.Color

/**
 * State Layer: Represents a candidate solution in the optimization process.
 *
 * A Mixture couples a static palette (list of colors) with dynamic weights.
 * The weights vector is the primary optimization variable that changes during optimization.
 *
 * Принцип: Состояние - это решение, и это единственное, что меняется во время оптимизации.
 *
 * @property palette The ordered list of available colors (immutable reference)
 * @property weights The proportions for each color (mutable during optimization)
 */
data class Mixture(
    val palette: List<Color>,
    val weights: DoubleArray
) {
    init {
        require(palette.size == weights.size) {
            "Palette size (${palette.size}) must match weights size (${weights.size})"
        }
    }

    /**
     * Creates a copy of this mixture with normalized weights.
     */
    fun withNormalizedWeights(normalizedWeights: DoubleArray): Mixture {
        require(normalizedWeights.size == weights.size) {
            "Normalized weights size must match original weights size"
        }
        return Mixture(palette, normalizedWeights)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Mixture) return false
        if (palette != other.palette) return false
        if (!weights.contentEquals(other.weights)) return false
        return true
    }

    override fun hashCode(): Int {
        var result = palette.hashCode()
        result = 31 * result + weights.contentHashCode()
        return result
    }
}

