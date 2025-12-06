package hyperparameter

data class HyperparameterSample(
    val populationMultiplier: Int,
    val sigma: Double,
    val diagonalOnly: Int,
    val checkFeasibleCount: Int,
    val stopFitness: Double,
    val meanError: Double
)
