package functional.error

import domain.LabColor
import kotlin.math.*

/**
 * MixingError Implementation: CIEDE2000 color difference formula.
 *
 * Industry-standard perceptual color difference metric that closely matches
 * human perception. More accurate than simpler ΔE76 formula.
 *
 * Reference: "The CIEDE2000 Color-Difference Formula" by Sharma, Wu, & Dalal
 *
 * @property kL Lightness weighting factor
 * @property kC Chroma weighting factor
 * @property kH Hue weighting factor
 */
class DeltaE2000(
    private val kL: Double = 1.0,
    private val kC: Double = 1.0,
    private val kH: Double = 1.0
) : MixingError {

    override fun calculate(mixed: LabColor, target: LabColor): Double {
        val (L1, a1, b1) = mixed
        val (L2, a2, b2) = target

        // Step 1: Chroma for each color
        val c1Val = sqrt(a1 * a1 + b1 * b1)
        val c2Val = sqrt(a2 * a2 + b2 * b2)

        // Step 2: Average chroma
        val cBar = (c1Val + c2Val) / 2.0

        // Step 3: G factor
        val cBar7 = cBar.pow(7.0)
        val g = 0.5 * (1.0 - sqrt(cBar7 / (cBar7 + 25.0.pow(7.0))))

        // Step 4: a' values
        val a1Prime = (1.0 + g) * a1
        val a2Prime = (1.0 + g) * a2

        // Step 5: C' values
        val c1Prime = sqrt(a1Prime * a1Prime + b1 * b1)
        val c2Prime = sqrt(a2Prime * a2Prime + b2 * b2)
        val cBarPrime = (c1Prime + c2Prime) / 2.0

        // Step 6: h' values (degrees)
        val h1Prime = hueAngleDegrees(a1Prime, b1)
        val h2Prime = hueAngleDegrees(a2Prime, b2)

        // Step 7: ΔL', ΔC', ΔH'
        val deltaLPrime = L2 - L1
        val deltaCPrime = c2Prime - c1Prime

        val deltaHPrime = if (c1Prime * c2Prime == 0.0) {
            0.0
        } else {
            var dh = h2Prime - h1Prime
            if (dh > 180.0) dh -= 360.0
            if (dh < -180.0) dh += 360.0
            2.0 * sqrt(c1Prime * c2Prime) * sinDeg(dh / 2.0)
        }

        // Step 8: Means
        val LBarPrime = (L1 + L2) / 2.0

        val hBarPrime = if (c1Prime * c2Prime == 0.0) {
            h1Prime + h2Prime
        } else {
            val diff = abs(h1Prime - h2Prime)
            when {
                diff > 180.0 && (h1Prime + h2Prime) < 360.0 -> (h1Prime + h2Prime + 360.0) / 2.0
                diff > 180.0 -> (h1Prime + h2Prime - 360.0) / 2.0
                else -> (h1Prime + h2Prime) / 2.0
            }
        }

        // Step 9: T factor
        val t = 1.0 -
                0.17 * cosDeg(hBarPrime - 30.0) +
                0.24 * cosDeg(2.0 * hBarPrime) +
                0.32 * cosDeg(3.0 * hBarPrime + 6.0) -
                0.20 * cosDeg(4.0 * hBarPrime - 63.0)

        // Step 10: Sl, Sc, Sh
        val sL = 1.0 + (0.015 * (LBarPrime - 50.0).pow(2.0)) /
                sqrt(20.0 + (LBarPrime - 50.0).pow(2.0))

        val sC = 1.0 + 0.045 * cBarPrime
        val sH = 1.0 + 0.015 * cBarPrime * t

        // Step 11: Δθ, Rc, Rt
        val deltaTheta = 30.0 * exp(-((hBarPrime - 275.0) / 25.0).pow(2.0))
        val rC = 2.0 * sqrt(cBarPrime.pow(7.0) / (cBarPrime.pow(7.0) + 25.0.pow(7.0)))
        val rT = -sinDeg(2.0 * deltaTheta) * rC

        // Final ΔE2000
        val lTerm = deltaLPrime / (kL * sL)
        val cTerm = deltaCPrime / (kC * sC)
        val hTerm = deltaHPrime / (kH * sH)

        return sqrt(
            lTerm * lTerm +
                    cTerm * cTerm +
                    hTerm * hTerm +
                    rT * cTerm * hTerm
        )
    }

    private fun hueAngleDegrees(a: Double, b: Double): Double {
        if (a == 0.0 && b == 0.0) return 0.0
        var angle = Math.toDegrees(atan2(b, a))
        if (angle < 0.0) angle += 360.0
        return angle
    }

    private fun sinDeg(deg: Double): Double = sin(Math.toRadians(deg))
    private fun cosDeg(deg: Double): Double = cos(Math.toRadians(deg))
}
