package functional.initialGuess

import domain.Color
import domain.LabColor
import functional.error.DeltaE2000

/**
 * Generates an initial guess based on the closest color in the palette.
 *
 * A similar color gets 0.5 weight and others 0.1.
 */
class SimilarityGuessGenerator (
    val palette: List<Color>,
    val targetLab: LabColor
) : InitialGuessGenerator() {
    override fun generateInitialGuess(dimensions: Int): DoubleArray {

        val mostSimilarColor = findSimilarColor()
        val mostSimilarIndex = palette.indexOf(mostSimilarColor)

        return generateSimilarityBasedGuess(dimensions, mostSimilarIndex)
    }


    private fun generateSimilarityBasedGuess(dimensions: Int, similarIndex: Int): DoubleArray {
        val guess = DoubleArray(dimensions)
        val similarWeight = 0.5
        val baseWeight = (1.0 - similarWeight) / (dimensions - 1)

        for (i in guess.indices) {
            guess[i] = if (i == similarIndex) similarWeight else baseWeight
        }

        return guess
    }

    private fun findSimilarColor(): Color {
        return palette.minByOrNull { color -> calculateColorDistance(color) }
            ?: throw IllegalStateException("Palette is empty")
    }

    private fun calculateColorDistance(color: Color): Double {
        return DeltaE2000().calculate(color.lab, targetLab)
    }
}
