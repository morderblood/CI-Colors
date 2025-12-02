package functional.error

import domain.LabColor

/**
 * Functional Layer: Computes perceptual distance between two colors.
 *
 * Pure function for color comparison.
 *
 * Different implementations:
 * - ΔE2000 (CIEDE2000 - most perceptually accurate)
 * - ΔE76 (simple Euclidean distance in LAB space)
 * - RMSE in RGB
 * - Custom perceptual metrics
 *
 * Принцип: Метрики ошибки должны быть чистыми функциями.
 */
interface MixingError {
    /**
     * Calculates the perceptual distance between two colors.
     *
     * @param mixed The mixed/candidate color
     * @param target The target color to match
     * @return Distance value (lower is better, 0 = perfect match)
     */
    fun calculate(mixed: LabColor, target: LabColor): Double
}
