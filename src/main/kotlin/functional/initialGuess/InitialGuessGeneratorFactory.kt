package functional.initialGuess

import domain.Color
import domain.LabColor
import functional.mixer.ColorMixer

object InitialGuessGeneratorFactory {
    fun createInitialGuessGenerator(
        generatorName: String,
        palette: List<Color>,
        targetLab: LabColor
    ): InitialGuessGenerator {
        return when (generatorName) {
            "Uniform" -> UniformInitialGuessGenerator()
            "Random" -> RandomInitialGuessGenerator()
            "Similarity" -> SimilarityGuessGenerator(palette, targetLab)
            else -> throw IllegalArgumentException("Unknown initial guess generator: $generatorName")
        }
    }
}