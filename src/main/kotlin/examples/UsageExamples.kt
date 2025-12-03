package examples

import data.Palette
import domain.Color
import domain.LabColor
import functional.error.DeltaE2000
import functional.error.DeltaE76
import functional.error.MixingError
import functional.mixer.LabBlendColorMixer
import functional.mixer.MixboxColorMixer
import functional.normalizer.ProportionsNormalizer
import functional.normalizer.SoftmaxNormalizer
import functional.normalizer.Normalizer
import functional.mixer.ColorMixer
import goal.Goal
import optimizer.BOBYQAOptimizerImpl
import optimizer.CMAESOptimizerImpl
import optimizer.HybridOptimizer
import optimizer.NelderMeadOptimizer
import optimizer.Optimizer
import optimizer.PowellOptimizerImpl
import penalty.Penalty
import penalty.SparsityPenalty
import penalty.SimilarityPenalty
import penalty.L1RegularizationPenalty
import penalty.L2RegularizationPenalty
import penalty.NegativesPenalty
import utils.WeightPresenter
import kotlin.compareTo
import kotlin.text.get

/**
 * USAGE EXAMPLES
 *
 * This file demonstrates how to use the refactored optimization framework.
 * It shows the clean separation of concerns and modular composition.
 */

fun example_bestOptimization() {
    val allErrors = mutableListOf<Triple<Double, String, DoubleArray>>()

    val palette = Palette.favoriteColors

    val targetColor = LabColor.fromHex("#7a928c")

    val normalizer: Normalizer = ProportionsNormalizer()

    val mixer: ColorMixer = MixboxColorMixer()

    //val errorMetric: MixingError = DeltaE76()
    val errorMetric: MixingError = DeltaE2000(kL = 1.0, kC = 1.0, kH = 1.0)

    val penalties = listOf<Penalty>(
        SparsityPenalty(threshold = 0.01, penaltyPerColor = 1.5)
    )
    val goal = Goal(
        palette = palette,
        target = targetColor,
        penalties = penalties,
        mixingError = errorMetric,
        normalizer = normalizer,
        colorMixer = mixer
    )

    val pureGoal = Goal(
        palette = palette,
        target = targetColor,
        penalties = emptyList(),  // Без штрафов
        mixingError = errorMetric,
        normalizer = normalizer,
        colorMixer = mixer
    )

    /*
    *
    * */
    val closestIndex = palette.indices
        .filter { i ->
            val lab = palette[i].lab
            // Чёрный: L < 10
            // Белый: L > 90
            // Серый: низкие a и b (ахроматичный)
            val isBlackOrWhite = lab.l < 10.0 || lab.l > 90.0
            val isGray = kotlin.math.abs(lab.a) < 5.0 && kotlin.math.abs(lab.b) < 5.0
            !(isBlackOrWhite || isGray)
        }.minByOrNull { i ->
        errorMetric.calculate(palette[i].lab, targetColor)
    } ?: 0
    var initialGuess = DoubleArray(palette.size) { i ->
        if (i == closestIndex) 0.9 else 0.1
    }

    //initialGuess = DoubleArray(palette.size) { 1.0 / palette.size }

    var optimizer : Optimizer = CMAESOptimizerImpl()
    var result = optimizer.optimize(goal, initialGuess)

    var finalError = pureGoal.evaluate(result.weights)

    val cmaesResult = result.copy(algorithmName = "CMA-ES")

    allErrors.add(Triple(finalError, result.algorithmName, result.weights))

    /*
    *
    * */

    //initialGuess = DoubleArray(palette.size) { 0.2 }

    optimizer = BOBYQAOptimizerImpl()
    result = optimizer.optimize(goal, initialGuess)

    finalError = pureGoal.evaluate(result.weights)

    allErrors.add(Triple(finalError, result.algorithmName, result.weights))

    /*
    *
    * */

    //initialGuess = DoubleArray(palette.size) { 1.0 / palette.size }

    optimizer = PowellOptimizerImpl()
    result = optimizer.optimize(goal, initialGuess)

    finalError = pureGoal.evaluate(result.weights)

    allErrors.add(Triple(finalError, result.algorithmName, result.weights))

    /*
    *
    * */

    //initialGuess = DoubleArray(palette.size) { 1.0 / palette.size }

    optimizer = NelderMeadOptimizer()
    result = optimizer.optimize(goal, initialGuess)

    finalError = pureGoal.evaluate(result.weights)

    allErrors.add(Triple(finalError, result.algorithmName, result.weights))

    for (error in allErrors) {
        val recipe = WeightPresenter.normalizeOptimizationResult(
            weights = error.third,
            palette = palette.take(error.third.size),  // Обрезаем палитру для соответствия размерам
            significanceThreshold = 0.1
        )
        println("${error.second}: ${error.first} : ${error.third.joinToString()}")
        println("  ${recipe.toFormattedString()}")
        var mix = mixer.mixColors(error.third, palette)
        println("  Mix: Hex=${mix.toHex()}")
        println()
    }
}

// Example 1: Basic color mixing optimization
fun example1_basicOptimization() {
    // Step 1: Define domain - immutable color palette
    val palette = Palette.allColors

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
    val optimizer = CMAESOptimizerImpl()
    val result = optimizer.optimize(goal, initialWeights)

    // Step 8: Evaluate the final solution
    val finalError = goal.evaluate(result.weights)
    println("Initial error: $finalError")
}

// Example 2: Using similarity penalties
fun example2_similarityPenalty() {
    val palette = Palette.allColors

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
    val palette = Palette.allColors
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
    val palette = Palette.allColors

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


