package hyperparameter

/**
 * Result of hyperparameter optimization.
 *
 * @param optimizerName Name of the inner optimizer being optimized
 * @param parameters Map of parameter names to their optimized values
 * @param meanError Mean error achieved with these parameters
 */
data class HyperparameterSample(
    val optimizerName: String,
    val parameters: Map<String, Any>,
    val meanError: Double
)
