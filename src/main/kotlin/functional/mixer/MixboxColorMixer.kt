package functional.mixer

import com.scrtwpns.Mixbox
import domain.Color
import domain.LabColor

/**
 * ColorMixer Implementation: Realistic pigment mixing using Mixbox library.
 *
 * Mixbox provides physically-accurate color mixing that simulates real paint behavior,
 * including subtractive color mixing effects.
 *
 * This implementation uses iterative pairwise mixing for multiple colors.
 *
 * TODO: Add Mixbox library dependency to build.gradle.kts
 * Repository: https://github.com/scrtwpns/mixbox
 */
class MixboxColorMixer : ColorMixer {

    override fun mixColors(weights: DoubleArray, palette: List<Color>): LabColor {
        require(weights.size == palette.size) {
            "Weights size must match palette size"
        }

        if (palette.isEmpty()) {
            // Return neutral gray for empty palette
            return LabColor.fromRgb(128, 128, 128)
        }

        // Create list of colors with their weights, filtering negligible contributions
        val weightedColors = weights.indices
            .map { i -> palette[i].lab.toInt() to weights[i] }
            .filter { it.second > 0.1 }
            .sortedByDescending { it.second }

        if (weightedColors.isEmpty()) {
            return LabColor.fromRgb(128, 128, 128)
        }

        if (weightedColors.size == 1) {
            // Single color - return it directly
            val hex = String.format("#%06X", 0xFFFFFF and weightedColors[0].first)
            return LabColor.fromHex(hex)
        }

        // Iteratively mix colors using Mixbox
        var mixedColor = weightedColors[0].first
        var totalWeight = weightedColors[0].second

        for (i in 1 until weightedColors.size) {
            val nextColor = weightedColors[i].first
            val nextWeight = weightedColors[i].second

            // Calculate mixing ratio
            val t = nextWeight / (totalWeight + nextWeight)

            // Use Mixbox for realistic pigment mixing
            mixedColor = Mixbox.lerp(mixedColor, nextColor, t.toFloat())
            totalWeight += nextWeight
        }

        // Convert result back to LabColor
        val hex = String.format("#%06X", 0xFFFFFF and mixedColor)
        return LabColor.fromHex(hex)
    }
}

