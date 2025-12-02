package functional.mixer

import domain.Color
import domain.LabColor

/**
 * Functional Layer: Computes resulting color from weights + palette.
 *
 * Pure function: deterministic and stable.
 *
 * Different implementations can use:
 * - Additive model
 * - LAB-blended model
 * - Realistic pigment mixing (e.g., Mixbox)
 *
 * Принцип: Миксер - это чистая функция без побочных эффектов.
 */
interface ColorMixer {
    /**
     * Mixes colors from a palette using given weights.
     *
     * @param weights Normalized proportions for each color (should sum to ~1.0)
     * @param palette The ordered list of colors corresponding to weights
     * @return The resulting mixed color in LAB color space
     */
    fun mixColors(weights: DoubleArray, palette: List<Color>): LabColor
}
