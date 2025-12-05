package evaluation

import domain.LabColor
import java.util.Locale

fun Double.round2(): Double =
    "%.2f".format(Locale.US, this).toDouble()


data class TrainingData(
    val targetLab: LabColor,
    val weights: DoubleArray,
)   : DataSetItem {
    override fun toCSVString(): String {
        val labStr = "${targetLab.l};${targetLab.a};${targetLab.b}"

        val weightsStr = weights.joinToString(";") { it.round2().toString() }
        return "$labStr,$weightsStr"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TrainingData) return false
        if (targetLab != other.targetLab) return false
        if (!weights.contentEquals(other.weights)) return false
        return true
    }
}