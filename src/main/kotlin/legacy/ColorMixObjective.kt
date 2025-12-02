package legacy

// TODO: This file is superseded by goal.Goal
// This implementation can be deleted after migrating to the new architecture

import domain.LabColor
import org.apache.commons.math3.analysis.MultivariateFunction

class ColorMixObjective(
    private val baseColors: List<LabColor>,
    private val target: LabColor,
    private val similarityPairs: List<Pair<Int, Int>>,
    private val lambdaSimilar: Double = 0.7,   // вес штрафа за похожие цвета
    private val lambdaSparse: Double = 0.5     // вес штрафа за количество пигментов
) : MultivariateFunction {

    override fun value(point: DoubleArray): Double {
        // 1. Нормализуем веса (>= 0, сумма = 1)
        // weight normalisation
        val w = MixtureOptimizer.normalizeWeights(point)

        // 2. Смешиваем цвета
        // mix colors
        val mixed = MixtureOptimizer.mixColors(w, baseColors)

        // 3. Базовая ошибка цвета (чем меньше, тем лучше)
        // base error
        val colorError = MixtureOptimizer.deltaE(mixed, target)

        // 4. Штраф за «запрещённые» пары похожих цветов
        // penalty for similar colors
        val simPenalty = Penalties.similarityPenalty(w, similarityPairs)

        // 5. Штраф за количество используемых пигментов
        // penalty for to many colors
        val sparsePenalty = Penalties.sparsityPenalty(w)

        // 6. Итоговая функция потерь
        // Final error
        return colorError + lambdaSimilar * simPenalty + lambdaSparse * sparsePenalty
    }
}
