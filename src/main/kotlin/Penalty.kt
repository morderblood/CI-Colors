abstract class Penalty (
    val proportions: List<Color>,
    val threshold: Double,
    val penaltyPerItem: Double,
) {
    abstract fun calculate(proportions: List<Color>): Double
}