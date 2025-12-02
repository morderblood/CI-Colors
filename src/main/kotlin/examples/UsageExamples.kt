package examples

import domain.Color
import domain.LabColor
import functional.error.DeltaE2000
import functional.error.DeltaE76
import functional.mixer.LabBlendColorMixer
import functional.mixer.MixboxColorMixer
import functional.normalizer.ProportionsNormalizer
import functional.normalizer.SoftmaxNormalizer
import functional.normalizer.Normalizer
import functional.mixer.ColorMixer
import goal.Goal
import penalty.Penalty
import penalty.SparsityPenalty
import penalty.SimilarityPenalty
import penalty.L1RegularizationPenalty
import penalty.L2RegularizationPenalty

/**
 * USAGE EXAMPLES
 *
 * This file demonstrates how to use the refactored optimization framework.
 * It shows the clean separation of concerns and modular composition.
 */

// Example 1: Basic color mixing optimization
fun example1_basicOptimization() {
    // Step 1: Define domain - immutable color palette
    val palette = listOf(
        Color(
            id = 1,
            title = "Vermilion Red",
            imageName = "vermilion.jpg",
            hex = "#E34234",
            lab = LabColor.fromHex("#E34234"),
            isFavorite = false
        ),
        Color(
            id = 2,
            title = "Ultramarine Blue",
            imageName = "ultramarine.jpg",
            hex = "#4166F5",
            lab = LabColor.fromHex("#4166F5"),
            isFavorite = true
        ),
        Color(
            id = 3,
            title = "Cadmium Yellow",
            imageName = "cadmium.jpg",
            hex = "#FFF600",
            lab = LabColor.fromHex("#FFF600"),
            isFavorite = false
        )
    )

    // Step 2: Define target color
    val targetColor = LabColor.fromHex("#8B4513") // Saddle Brown

    // Step 3: Configure functional components
    val normalizer: Normalizer = ProportionsNormalizer()
    val mixer: ColorMixer = MixboxColorMixer()
    val errorMetric: MixingError = DeltaE2000(kL = 1.0, kC = 1.0, kH = 1.0)

    // Step 4: Configure penalties
    val penalties = listOf<Penalty>(
        SparsityPenalty(threshold = 0.01, penaltyPerColor = 0.5),
        L2RegularizationPenalty(lambda = 0.1)
    )

    // Step 5: Build goal function
    val goal = Goal(
        palette = palette,
        target = targetColor,
        penalties = penalties,
        mixingError = errorMetric,
        normalizer = normalizer,
        colorMixer = mixer
    )

    // Step 6: Create initial guess (uniform distribution)
    val initialWeights = DoubleArray(palette.size) { 1.0 / palette.size }

    // Step 7: Optimize (assuming you have an optimizer implementation)
    // val optimizer = CMAESOptimizer(...)
    // val result = optimizer.optimize(goal, initialWeights)

    // Step 8: Evaluate final solution
    val finalError = goal.evaluate(initialWeights)
    println("Initial error: $finalError")
}

// Example 2: Using similarity penalties
fun example2_similarityPenalty() {
    val palette = listOf(
        Color(1, "Vermilion Red", "v.jpg", "#E34234", lab = LabColor.fromHex("#E34234")),
        Color(2, "Rouge Carmine Red", "r.jpg", "#DC143C", lab = LabColor.fromHex("#DC143C")),
        Color(3, "Phthalo Blue", "p.jpg", "#000F89", lab = LabColor.fromHex("#000F89")),
        Color(4, "Prussian Blue", "pr.jpg", "#003153", lab = LabColor.fromHex("#003153"))
    )

    // Define similarity pairs by index
    val similarityPairs = listOf(
        0 to 1, // Vermilion Red and Rouge Carmine Red are similar
        2 to 3  // Phthalo Blue and Prussian Blue are similar
    )

    val penalties = listOf<Penalty>(
        SimilarityPenalty(
            similarityPairs = similarityPairs,
            threshold = 0.1,
            penaltyPerPair = 3.0
        ),
        SparsityPenalty(threshold = 0.01, penaltyPerColor = 1.0)
    )

    val goal = Goal(
        palette = palette,
        target = LabColor.fromHex("#8B0000"),
        penalties = penalties,
        mixingError = DeltaE2000(),
        normalizer = ProportionsNormalizer(),
        colorMixer = MixboxColorMixer()
    )

    // This goal will penalize solutions that use both similar reds simultaneously
}

// Example 3: Comparing different color mixing strategies
fun example3_compareMixers() {
    val palette = listOf(
        Color(1, "Red", "r.jpg", "#FF0000", lab = LabColor.fromHex("#FF0000")),
        Color(2, "Blue", "b.jpg", "#0000FF", lab = LabColor.fromHex("#0000FF"))
    )
    val target = LabColor.fromHex("#800080") // Purple
    val weights = doubleArrayOf(0.5, 0.5)

    // Strategy 1: Mixbox (realistic pigment mixing)
    val mixbox = MixboxColorMixer()
    val mixboxResult = mixbox.mixColors(weights, palette)
    println("Mixbox result: L=${mixboxResult.l}, a=${mixboxResult.a}, b=${mixboxResult.b}")

    // Strategy 2: LAB blend (simple average)
    val labBlend = LabBlendColorMixer()
    val labBlendResult = labBlend.mixColors(weights, palette)
    println("LAB blend result: L=${labBlendResult.l}, a=${labBlendResult.a}, b=${labBlendResult.b}")

    // Results will differ - Mixbox is more realistic for paint mixing
}

// Example 4: Different normalizers
fun example4_normalizers() {
    val rawWeights = doubleArrayOf(2.0, -0.5, 1.0, 0.0)

    // Strategy 1: Sum-to-1 normalization
    val sumNormalizer = ProportionsNormalizer()
    val normalized1 = sumNormalizer.normalize(rawWeights)
    println("Sum normalized: ${normalized1.joinToString()}")
    // Output: [0.8, 0.0, 0.4, 0.0] normalized to sum=1

    // Strategy 2: Softmax normalization
    val softmaxNormalizer = SoftmaxNormalizer()
    val normalized2 = softmaxNormalizer.normalize(rawWeights)
    println("Softmax normalized: ${normalized2.joinToString()}")
    // Output: weights with exponential emphasis on larger values
}

// Example 5: Multiple penalties
fun example5_multiplePenalties() {
    val palette = (1..10).map { i ->
        Color(i, "Color$i", "c$i.jpg", "#000000", lab = LabColor.fromRgb(i * 25, 128, 128))
    }

    val penalties = listOf<Penalty>(
        SparsityPenalty(threshold = 0.01, penaltyPerColor = 2.0),    // Prefer fewer colors
        L1RegularizationPenalty(lambda = 0.5),                       // Encourage sparsity
        L2RegularizationPenalty(lambda = 0.1)                        // Smooth weights
    )

    val goal = Goal(
        palette = palette,
        target = LabColor.fromRgb(100, 150, 200),
        penalties = penalties,
        mixingError = DeltaE76(), // Fast metric for this example
        normalizer = ProportionsNormalizer(),
        colorMixer = LabBlendColorMixer()
    )

    // This goal balances color accuracy with sparsity and regularization
}

// Example 6: Extensibility - Custom penalty
class MaxWeightPenalty(
    private val maxAllowedWeight: Double = 0.8,
    private val penaltyStrength: Double = 10.0
) : Penalty {
    override fun calculate(weights: DoubleArray): Double {
        val maxWeight = weights.maxOrNull() ?: 0.0
        return if (maxWeight > maxAllowedWeight) {
            penaltyStrength * (maxWeight - maxAllowedWeight)
        } else {
            0.0
        }
    }
}

fun example6_customPenalty() {
    val penalties = listOf<Penalty>(
        MaxWeightPenalty(maxAllowedWeight = 0.7, penaltyStrength = 5.0),
        SparsityPenalty(threshold = 0.05, penaltyPerColor = 1.0)
    )

    // Easy to add custom penalties without modifying existing code!
}


