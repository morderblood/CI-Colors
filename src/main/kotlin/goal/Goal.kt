package goal

import domain.Color
import domain.LabColor
import functional.error.MixingError
import functional.mixer.ColorMixer
import functional.normalizer.Normalizer
import penalty.Penalty

/**
 * Goal Layer: Combines all components into a single evaluation function for optimization.
 *
 * This is the ONLY interface that optimizers interact with.
 *
 * The Goal is a pure function that:
 * 1. Normalizes weights
 * 2. Performs color mixing
 * 3. Computes base error (distance to target)
 * 4. Applies penalties
 * 5. Returns final scalar value
 *
 * Принцип: Goal - это единственный интерфейс, с которым взаимодействует оптимизатор.
 *
 * @property palette The ordered list of available colors
 * @property target The target color to match
 * @property penalties List of penalty functions to apply
 * @property mixingError The color distance metric
 * @property normalizer The weight normalization strategy
 * @property colorMixer The color mixing strategy
 */
class Goal(
    private val palette: List<Color>,
    private val target: LabColor,
    private val penalties: List<Penalty>,
    private val mixingError: MixingError,
    private val normalizer: Normalizer,
    private val colorMixer: ColorMixer
)  {
    /**
     * Evaluates a candidate solution (weight vector).
     *
     * This is a pure function with no side effects.
     *
     * @param weights Raw weight vector to evaluate
     * @return Objective value (lower is better)
     */
    fun evaluate(weights: DoubleArray): Double {
        require(weights.size == palette.size) {
            "Weights size (${weights.size}) must match palette size (${palette.size})"
        }

        // Step 1: Normalize weights
        val normalizedWeights = normalizer.normalize(weights)

        // Step 2: Mix colors
        val mixedColor = colorMixer.mixColors(normalizedWeights, palette)

        // Step 3: Calculate base error (color distance)
        var totalError = mixingError.calculate(mixedColor, target)

        // Step 4: Apply penalties
        for (penalty in penalties) {
            totalError += penalty.calculate(normalizedWeights)
        }

        return totalError
    }
}