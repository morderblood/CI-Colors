package functional.mixer

import domain.Color
import domain.LabColor

/**
 * ColorMixer Implementation: Simple weighted average in LAB color space.
 *
 * This is a simpler, faster alternative to Mixbox that performs linear
 * interpolation directly in LAB space. Less physically accurate but
 * computationally efficient.
 *
 * Good for quick prototyping or when the Mixbox library is not available.
 */
class LabBlendColorMixer : ColorMixer {

    override fun mixColors(weights: DoubleArray, palette: List<Color>): LabColor {
        require(weights.size == palette.size) {
            "Weights size must match palette size"
        }

        if (palette.isEmpty()) {
            return LabColor.fromRgb(128, 128, 128)
        }

        var sumL = 0.0
        var sumA = 0.0
        var sumB = 0.0
        var totalWeight = 0.0

        for (i in weights.indices) {
            if (weights[i] > 1e-10) {
                val lab = palette[i].lab
                sumL += lab.l * weights[i]
                sumA += lab.a * weights[i]
                sumB += lab.b * weights[i]
                totalWeight += weights[i]
            }
        }

        return if (totalWeight > 0.0) {
            LabColor(
                l = sumL / totalWeight,
                a = sumA / totalWeight,
                b = sumB / totalWeight
            )
        } else {
            LabColor.fromRgb(128, 128, 128)
        }
    }
}

