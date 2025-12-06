package evaluation

import domain.LabColor
import functional.normalizer.Normalizer
import penalty.Penalty

data class OptimizationResultData (
    val targetLab: LabColor,
    val resultLab: LabColor,
    val initialLab: LabColor,
    val targetWeights: DoubleArray,
    val resultWeights: DoubleArray,
    val initialWeights: DoubleArray,
    val optimizerName: String,
    val numberOfEvaluations: Int,
    val mixingErrorName: String,
    val penalties: List<Penalty>,
    val normalizer: Normalizer,
    val initialGuessType: String,
    val runtimeMs: Long,
    val converged: Boolean,
    val optimizationParameters: Map<String, Any>
)  : DataSetItem {
    override fun toCSVString(): String {
        val targetLabStr = "${targetLab.l};${targetLab.a};${targetLab.b}"
        val resultLabStr = "${resultLab.l};${resultLab.a};${resultLab.b}"
        val initialLabStr = "${initialLab.l};${initialLab.a};${initialLab.b}"

        val targetWeightsStr = targetWeights.joinToString(";") { it.toString() }
        val resultWeightsStr = resultWeights.joinToString(";") { it.toString() }
        val initialWeightsStr = initialWeights.joinToString(";") { it.toString() }

        val normalizer = normalizer.javaClass.simpleName

        val penaltiesStr = penalties.joinToString(";") { it.javaClass.simpleName }

        val parameterValues = optimizationParameters.values.joinToString(",") { it.toString() }

        val result = (arrayOf(
            targetLabStr,
            resultLabStr,
            initialLabStr,
            targetWeightsStr,
            resultWeightsStr,
            initialWeightsStr,
            optimizerName,
            numberOfEvaluations.toString(),
            mixingErrorName,
            penaltiesStr,
            normalizer,
            initialGuessType,
            runtimeMs.toString(),
            converged.toString()
        ) + parameterValues).joinToString(",")

        return result
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