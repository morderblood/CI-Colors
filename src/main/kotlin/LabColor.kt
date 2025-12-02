import kotlin.math.pow
import kotlin.math.roundToInt

data class RgbColor(val r: Int, val g: Int, val b: Int)
data class LabColor(val l: Double, val a: Double, val b: Double) {

    /**
     * Converts the Lab color instance to a HEX string.
     */

    fun toRgb(): RgbColor {
        // Step 1: Lab to XYZ (D65)
        val xn = 0.95047
        val yn = 1.0
        val zn = 1.08883

        fun fInv(t: Double): Double {
            val delta = 6.0 / 29.0
            return if (t > delta) t.pow(3) else 3 * delta.pow(2) * (t - 4.0 / 29.0)
        }

        val fy = (l + 16.0) / 116.0
        val fx = a / 500.0 + fy
        val fz = fy - b / 200.0

        val x = xn * fInv(fx)
        val y = yn * fInv(fy)
        val z = zn * fInv(fz)

        // Step 2: XYZ to linear sRGB
        val rLinear = 3.2406 * x - 1.5372 * y - 0.4986 * z
        val gLinear = -0.9689 * x + 1.8758 * y + 0.0415 * z
        val bLinear = 0.0557 * x - 0.2040 * y + 1.0570 * z

        // Step 3: Linear to gamma-corrected sRGB
        fun gammaCorrect(c: Double): Double {
            return if (c <= 0.0031308) 12.92 * c else 1.055 * c.pow(1.0 / 2.4) - 0.055
        }

        val rSrgb = gammaCorrect(rLinear)
        val gSrgb = gammaCorrect(gLinear)
        val bSrgb = gammaCorrect(bLinear)

        // Step 4: Scale to 0-255 range and return as RgbColor
        val r = (rSrgb * 255).roundToInt().coerceIn(0, 255)
        val g = (gSrgb * 255).roundToInt().coerceIn(0, 255)
        val b = (bSrgb * 255).roundToInt().coerceIn(0, 255)

        return RgbColor(r, g, b)
    }
    fun toInt(): Int {
        // Step 1: Lab to XYZ (D65) - Same logic as toHex()
        val xn = 0.95047
        val yn = 1.0
        val zn = 1.08883

        fun fInv(t: Double): Double {
            val delta = 6.0 / 29.0
            return if (t > delta) t.pow(3) else 3 * delta.pow(2) * (t - 4.0 / 29.0)
        }

        val fy = (l + 16.0) / 116.0
        val fx = a / 500.0 + fy
        val fz = fy - b / 200.0

        val x = xn * fInv(fx)
        val y = yn * fInv(fy)
        val z = zn * fInv(fz)

        // Step 2: XYZ to linear sRGB
        val rLinear = 3.2406 * x - 1.5372 * y - 0.4986 * z
        val gLinear = -0.9689 * x + 1.8758 * y + 0.0415 * z
        val bLinear = 0.0557 * x - 0.2040 * y + 1.0570 * z

        // Step 3: Linear to gamma-corrected sRGB
        fun gammaCorrect(c: Double): Double {
            return if (c <= 0.0031308) 12.92 * c else 1.055 * c.pow(1.0 / 2.4) - 0.055
        }

        val rSrgb = gammaCorrect(rLinear)
        val gSrgb = gammaCorrect(gLinear)
        val bSrgb = gammaCorrect(bLinear)

        // Step 4: Scale to 0-255 range
        val r = (rSrgb * 255).roundToInt().coerceIn(0, 255)
        val g = (gSrgb * 255).roundToInt().coerceIn(0, 255)
        val b = (bSrgb * 255).roundToInt().coerceIn(0, 255)

        // Step 5: Pack into an ARGB Int
        // Alpha is set to 255 (fully opaque)
        return (255 shl 24) or (r shl 16) or (g shl 8) or b
    }

    fun toHex(): String {
        // Step 1: Lab to XYZ (D65)
        val xn = 0.95047
        val yn = 1.0
        val zn = 1.08883

        fun fInv(t: Double): Double {
            val delta = 6.0 / 29.0
            return if (t > delta) t.pow(3) else 3 * delta.pow(2) * (t - 4.0 / 29.0)
        }

        val fy = (l + 16.0) / 116.0
        val fx = a / 500.0 + fy
        val fz = fy - b / 200.0

        val x = xn * fInv(fx)
        val y = yn * fInv(fy)
        val z = zn * fInv(fz)

        // Step 2: XYZ to linear sRGB
        val rLinear = 3.2406 * x - 1.5372 * y - 0.4986 * z
        val gLinear = -0.9689 * x + 1.8758 * y + 0.0415 * z
        val bLinear = 0.0557 * x - 0.2040 * y + 1.0570 * z

        // Step 3: Linear to gamma-corrected sRGB
        fun gammaCorrect(c: Double): Double {
            return if (c <= 0.0031308) 12.92 * c else 1.055 * c.pow(1.0 / 2.4) - 0.055
        }

        val rSrgb = gammaCorrect(rLinear)
        val gSrgb = gammaCorrect(gLinear)
        val bSrgb = gammaCorrect(bLinear)

        // Step 4: Scale and format
        val r = (rSrgb * 255).roundToInt().coerceIn(0, 255)
        val g = (gSrgb * 255).roundToInt().coerceIn(0, 255)
        val b = (bSrgb * 255).roundToInt().coerceIn(0, 255)

        return String.format("#%02x%02x%02x", r, g, b)
    }

    companion object {
        fun fromLab(lab: Map<String, Double>): LabColor {
            return LabColor(lab["l"]!!, lab["a"]!!, lab["b"]!!)
        }
        /**
         * Creates a LabColor instance from a HEX string (e.g., "#RRGGBB" or "#AARRGGBB").
         */
        fun fromHex(hex: String): LabColor {
            val clean = hex.removePrefix("#")

            val r: Int
            val g: Int
            val b: Int

            when (clean.length) {
                6 -> { // RRGGBB
                    r = clean.substring(0, 2).toInt(16)
                    g = clean.substring(2, 4).toInt(16)
                    b = clean.substring(4, 6).toInt(16)
                }
                8 -> { // AARRGGBB
                    r = clean.substring(2, 4).toInt(16)
                    g = clean.substring(4, 6).toInt(16)
                    b = clean.substring(6, 8).toInt(16)
                }
                else -> {
                    throw IllegalArgumentException("Invalid HEX color format: $hex. Must be 6 or 8 characters long, excluding the '#'.")
                }
            }
            return fromRgb(r, g, b)
        }

        /**
         * Creates a LabColor instance from sRGB components (0-255).
         */
        fun fromRgb(r: Int, g: Int, b: Int): LabColor {
            // Convert to [0,1] range
            var rr = r / 255.0
            var gg = g / 255.0
            var bb = b / 255.0

            fun f(c: Double): Double =
                if (c <= 0.04045) c / 12.92 else ((c + 0.055) / 1.055).pow(2.4)

            rr = f(rr)
            gg = f(gg)
            bb = f(bb)

            // RGB to XYZ (D65)
            val x = rr * 0.4124 + gg * 0.3576 + bb * 0.1805
            val y = rr * 0.2126 + gg * 0.7152 + bb * 0.0722
            val z = rr * 0.0193 + gg * 0.1192 + bb * 0.9505

            // XYZ to Lab (D65)
            val xn = 0.95047
            val yn = 1.00000
            val zn = 1.08883

            fun f2(t: Double): Double =
                if (t > 0.008856) t.pow(1.0 / 3.0) else (7.787 * t + 16.0 / 116.0)

            val fx = f2(x / xn)
            val fy = f2(y / yn)
            val fz = f2(z / zn)

            val l = 116 * fy - 16
            val a = 500 * (fx - fy)
            val b = 200 * (fy - fz)

            return LabColor(l, a, b)
        }
    }
}
