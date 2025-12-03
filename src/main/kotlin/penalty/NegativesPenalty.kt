package penalty

class NegativesPenalty (
    private val threshold: Double = 0.1
    ) : Penalty {
    override fun calculate(weights: DoubleArray): Double {
        var penalty = 0.0
        for (weight in weights) {
            penalty += weight.coerceAtLeast(0.0)
        }
        return penalty * threshold
    }
}