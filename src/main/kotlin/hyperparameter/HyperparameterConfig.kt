package hyperparameter

/**
 * Configuration for a single hyperparameter.
 *
 * @param name Parameter name as used by the optimizer
 * @param initialValue Initial value for optimization
 * @param sigma Standard deviation for CMA-ES
 * @param lowerBound Lower bound for the parameter
 * @param upperBound Upper bound for the parameter
 * @param transform Function to transform the continuous optimization value to the actual parameter value
 */
data class HyperparameterConfig(
    val name: String,
    val initialValue: Double,
    val sigma: Double,
    val lowerBound: Double,
    val upperBound: Double,
    val transform: (Double) -> Any = { it }
)

