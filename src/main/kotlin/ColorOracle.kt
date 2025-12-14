// kotlin
import data.Palette
import domain.Color
import domain.LabColor
import functional.mixer.MixboxColorMixer
import functional.normalizer.ProportionsNormalizer
import functional.error.MixingErrorFactory
import functional.initialGuess.InitialGuessGeneratorFactory
import optimizer.OptimizerFactory
import penalty.SparsityPenalty
import penalty.Penalty
import goal.Goal

class ColorOracle {
    /**
     * Предсказать веса смеси для заданного целевого Lab цвета.
     *
     * @param targetLab целевой цвет в Lab (DoubleArray)
     * @param palette палитра цветов (по умолчанию Palette.allColors)
     * @param optimizerName имя оптимизатора (по умолчанию "CMA-ES")
     * @param errorName имя ошибки смешивания (по умолчанию "DeltaE2000")
     * @param includeSparsityPenalty включать ли штраф за разреженность
     * @param initialGuessType тип начального приближения ("Uniform", "Random", "Similar")
     * @param optimizationParameters параметры оптимизатора (по умолчанию стандартные для CMA-ES)
     * @return массив весов (DoubleArray) размером palette.size
     */
    fun predictMixture(
        targetLab: LabColor,
        palette: List<Color> = Palette.allColors,
        optimizerName: String = "CMA-ES",
        errorName: String = "DeltaE2000",
        includeSparsityPenalty: Boolean = true,
        initialGuessType: String = "Uniform",
        optimizationParameters: Map<String, Any>? = mapOf(
            "populationMultiplier" to 12,
            "sigma" to 0.2,
            "diagonalOnly" to 12,
            "checkFeasibleCount" to 9,
            "stopFitness" to 0.002
        )
    ): DoubleArray {
        val mixer = MixboxColorMixer()
        val normalizer = ProportionsNormalizer()
        val mixingError = MixingErrorFactory.createMixingError(errorName)

        val penalties = mutableListOf<Penalty>()
        if (includeSparsityPenalty) {
            penalties.add(SparsityPenalty(threshold = 0.01, penaltyPerColor = 20.0))
        }

        val goal = Goal(
            palette,
            targetLab,
            penalties,
            mixingError,
            normalizer,
            mixer
        )

        val initialGuess = InitialGuessGeneratorFactory
            .createInitialGuessGenerator(
                generatorName = initialGuessType,
                palette = palette,
                targetLab = targetLab
            )
            .generateInitialGuess(dimensions = palette.size)

        return try {
            val optimizer = OptimizerFactory.createOptimizer(optimizerName, optimizationParameters ?: emptyMap())
            val result = optimizer.optimize(goal, initialGuess)
            result.weights
        } catch (e: Exception) {
            // при ошибке возвращаем начальное приближение (без изменений)
            initialGuess
        }
    }
}
