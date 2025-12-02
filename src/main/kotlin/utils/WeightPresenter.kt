package utils

import domain.Color
import kotlin.math.round

/**
 * Utility functions for presenting optimization results in a user-friendly way.
 */
object WeightPresenter {

    /**
     * Result of weight normalization for presentation.
     *
     * @property colors List of colors with significant weights
     * @property parts Integer ratio parts for each color (e.g., "2 parts Red, 1 part Blue")
     * @property isEmpty True if no significant colors were found
     */
    data class PresentableRecipe(
        val colors: List<Color>,
        val parts: List<Int>,
        val isEmpty: Boolean = false
    ) {
        /**
         * Get formatted recipe string.
         * Example: "2 parts Yellow Ochre, 1 part Vermilion Red"
         */
        fun toFormattedString(): String {
            if (isEmpty) return "No significant colors"

            return colors.zip(parts)
                .joinToString(", ") { (color, part) ->
                    "$part ${if (part == 1) "part" else "parts"} ${color.title}"
                }
        }

        /**
         * Get list of strings for display in UI.
         * Example: ["2 parts Yellow Ochre", "1 part Vermilion Red"]
         */
        fun toDisplayList(): List<String> {
            if (isEmpty) return emptyList()

            return colors.zip(parts).map { (color, part) ->
                "$part ${if (part == 1) "part" else "parts"} ${color.title}"
            }
        }

        /**
         * Get map of color to parts.
         */
        fun toMap(): Map<Color, Int> {
            if (isEmpty) return emptyMap()
            return colors.zip(parts).toMap()
        }
    }

    /**
     * Normalize weights to small integer ratios for presentation.
     *
     * Filters out insignificant weights, then converts to simple integer ratios
     * like "2 parts Red, 1 part Blue" instead of "0.6667 Red, 0.3333 Blue".
     *
     * @param colors List of colors with their weights
     * @param getWeight Function to extract weight from a color
     * @param significanceThreshold Minimum weight to be considered significant (default 0.05 = 5%)
     * @return PresentableRecipe with normalized integer parts
     */
    fun <T> normalizeForPresentation(
        colors: List<T>,
        getWeight: (T) -> Double,
        significanceThreshold: Double = 0.05
    ): PresentableRecipe where T : Any {
        // Filter significant colors
        val significantColors = colors.filter { getWeight(it) > significanceThreshold }

        if (significantColors.isEmpty()) {
            return PresentableRecipe(emptyList(), emptyList(), isEmpty = true)
        }

        // Find smallest weight
        val smallest = significantColors.minOf { getWeight(it) }

        // Convert to integer ratios
        val colorsWithParts = significantColors.mapNotNull { item ->
            val parts = round(getWeight(item) / smallest).toInt()
            if (parts < 1) null else (item to parts)
        }

        if (colorsWithParts.isEmpty()) {
            return PresentableRecipe(emptyList(), emptyList(), isEmpty = true)
        }

        // Cast to Color (assuming T is Color or has Color)
        @Suppress("UNCHECKED_CAST")
        val resultColors = colorsWithParts.map { (item, _) -> item as Color }
        val resultParts = colorsWithParts.map { (_, parts) -> parts }

        return PresentableRecipe(resultColors, resultParts)
    }

    /**
     * Normalize weights for Color objects directly.
     *
     * @param colors List of colors with weights in a "weight" or "part" property
     * @param weightExtractor Function to extract weight (default assumes a weight property exists)
     * @param significanceThreshold Minimum weight to be considered significant
     * @return PresentableRecipe with normalized integer parts
     */
    fun normalizeColorWeights(
        colors: List<Pair<Color, Double>>,
        significanceThreshold: Double = 0.05
    ): PresentableRecipe {
        // Filter significant colors
        val significantColors = colors.filter { it.second > significanceThreshold }

        if (significantColors.isEmpty()) {
            return PresentableRecipe(emptyList(), emptyList(), isEmpty = true)
        }

        // Find smallest weight
        val smallest = significantColors.minOf { it.second }

        // Convert to integer ratios
        val colorsWithParts = significantColors.mapNotNull { (color, weight) ->
            val parts = round(weight / smallest).toInt()
            if (parts < 1) null else (color to parts)
        }

        if (colorsWithParts.isEmpty()) {
            return PresentableRecipe(emptyList(), emptyList(), isEmpty = true)
        }

        val resultColors = colorsWithParts.map { (color, _) -> color }
        val resultParts = colorsWithParts.map { (_, parts) -> parts }

        return PresentableRecipe(resultColors, resultParts)
    }

    /**
     * Normalize weights from optimization result (DoubleArray + palette).
     *
     * @param weights Optimization result weights
     * @param palette Corresponding color palette
     * @param significanceThreshold Minimum weight to be considered significant
     * @return PresentableRecipe with normalized integer parts
     */
    fun normalizeOptimizationResult(
        weights: DoubleArray,
        palette: List<Color>,
        significanceThreshold: Double = 0.05
    ): PresentableRecipe {
        require(weights.size == palette.size) {
            "Weights size (${weights.size}) must match palette size (${palette.size})"
        }

        val colorWeightPairs = palette.zip(weights.toList())
        return normalizeColorWeights(colorWeightPairs, significanceThreshold)
    }

    /**
     * Simplify a ratio by finding GCD (Greatest Common Divisor).
     *
     * Example: [4, 2, 6] -> [2, 1, 3]
     *
     * @param parts List of integer parts
     * @return Simplified parts
     */
    fun simplifyRatio(parts: List<Int>): List<Int> {
        if (parts.isEmpty() || parts.all { it == 0 }) return parts

        val gcd = parts.reduce { a, b -> gcd(a, b) }
        return if (gcd > 1) {
            parts.map { it / gcd }
        } else {
            parts
        }
    }

    /**
     * Calculate GCD (Greatest Common Divisor) of two numbers.
     */
    private fun gcd(a: Int, b: Int): Int {
        return if (b == 0) a else gcd(b, a % b)
    }

    /**
     * Format weights as percentages.
     *
     * @param weights Normalized weights (should sum to 1.0)
     * @return List of percentage strings (e.g., "33.3%")
     */
    fun formatAsPercentages(weights: DoubleArray): List<String> {
        return weights.map { "${"%.1f".format(it * 100)}%" }
    }

    /**
     * Format a single color-weight pair for display.
     *
     * @param color The color
     * @param weight The weight (0.0 to 1.0)
     * @param showPercentage If true, show percentage; if false, show decimal
     * @return Formatted string
     */
    fun formatColorWeight(color: Color, weight: Double, showPercentage: Boolean = true): String {
        return if (showPercentage) {
            "${color.title}: ${"%.1f".format(weight * 100)}%"
        } else {
            "${color.title}: ${"%.4f".format(weight)}"
        }
    }
}

