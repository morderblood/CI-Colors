package evaluation

import domain.LabColor
import functional.error.MixingError
import functional.normalizer.Normalizer
import optimizer.Optimizer
import penalty.Penalty

data class OptimizationResultData (
    val targetLab: LabColor,
    val resultLab: LabColor,
    val targetWeights: DoubleArray,
    val resultWeights: DoubleArray,
    val optimizer: Optimizer,
    val numberOfEvaluations: Int,
    val mixingError: MixingError,
    val penalties: List<Penalty>,
    val normalizer: Normalizer,
    val initialGuessType: String
)  : DataSetItem {
    override fun toCSVString(): String {
        val targetLabStr = "${targetLab.l};${targetLab.a};${targetLab.b}"
        val resultLabStr = "${resultLab.l};${resultLab.a};${resultLab.b}"

        val targetWeightsStr = targetWeights.joinToString(";") { it.toString() }
        val resultWeightsStr = resultWeights.joinToString(";") { it.toString() }

        val optimizerName = optimizer.javaClass.simpleName
        val errorName = mixingError.javaClass.simpleName
        val normalizer = normalizer.javaClass.simpleName

        val penaltisStr = penalties.joinToString(";") { it.javaClass.simpleName }

        return "$targetLabStr,$resultLabStr,$targetWeightsStr,$resultWeightsStr,$optimizerName,$numberOfEvaluations,$errorName,$penaltisStr,$normalizer,$initialGuessType"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is OptimizationResultData) return false
        if (targetLab != other.targetLab) return false
        if (!targetWeights.contentEquals(other.targetWeights)) return false
        return true
    }

    override fun hashCode(): Int {
        return targetLab.hashCode()
    }
}