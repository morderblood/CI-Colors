package functional.error

import domain.LabColor
import kotlin.math.sqrt

/**
 * MixingError Implementation: Simple Euclidean distance in LAB space (ΔE76).
 *
 * The original CIE76 color difference formula. Simpler and faster than
 * CIEDE2000 but less perceptually accurate.
 *
 * Formula: ΔE = sqrt((L2-L1)² + (a2-a1)² + (b2-b1)²)
 *
 * Good for:
 * - Quick prototyping
 * - Cases where speed matters more than perceptual accuracy
 * - Initial optimization before refinement with ΔE2000
 */
class DeltaE76 : MixingError {

    override fun calculate(mixed: LabColor, target: LabColor): Double {
        val dl = target.l - mixed.l
        val da = target.a - mixed.a
        val db = target.b - mixed.b

        return sqrt(dl * dl + da * da + db * db)
    }
}

