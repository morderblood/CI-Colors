package examples

import data.Palette
import domain.Color
import utils.WeightPresenter
import kotlin.math.roundToInt

/**
 * Demonstrates how to present optimization results in a user-friendly way.
 */
fun demonstrateWeightPresentation() {
    println("🎨 Weight Presentation Examples")
    println("=".repeat(60))

    // Example 1: From optimization result (DoubleArray + palette)
    println("\n📊 Example 1: Optimization Result")
    println("-".repeat(60))

    val palette = Palette.favoriteColors
    val optimizedWeights = doubleArrayOf(
        0.45,  // Yellow Ochre
        0.32,  // Vermilion Red
        0.15,  // Ultramarine Blue
        0.06,  // Rouge Carmine Red
        0.02,  // Titanium White
        0.00,  // Phthalo Blue
        0.00   // Black
    )

    println("Raw weights:")
    palette.take(optimizedWeights.size).forEachIndexed { i, color ->
        println("  ${color.title}: ${optimizedWeights[i]}")
    }

    val recipe = WeightPresenter.normalizeOptimizationResult(
        weights = optimizedWeights,
        palette = palette.take(optimizedWeights.size),  // Обрезаем палитру для соответствия размерам
        significanceThreshold = 0.05
    )

    println("\n✨ Presentable Recipe:")
    if (recipe.isEmpty) {
        println("  No significant colors found")
    } else {
        println("  ${recipe.toFormattedString()}")
        println("\n  Display list:")
        recipe.toDisplayList().forEach { println("    • $it") }
    }

    // Example 2: From Color pairs (Color + weight)
    println("\n" + "=".repeat(60))
    println("📊 Example 2: Color-Weight Pairs")
    println("-".repeat(60))

    val colorWeightPairs = listOf(
        Palette.getByTitle("Yellow Ochre")!! to 0.667,
        Palette.getByTitle("Vermilion Red")!! to 0.333,
        Palette.getByTitle("Black")!! to 0.02  // Too small, will be filtered
    )

    println("Input:")
    colorWeightPairs.forEach { (color, weight) ->
        println("  ${color.title}: $weight")
    }

    val recipe2 = WeightPresenter.normalizeColorWeights(
        colors = colorWeightPairs,
        significanceThreshold = 0.05
    )

    println("\n✨ Presentable Recipe:")
    println("  ${recipe2.toFormattedString()}")

    // Example 3: Simplified ratios
    println("\n" + "=".repeat(60))
    println("📊 Example 3: Ratio Simplification")
    println("-".repeat(60))

    val parts1 = listOf(4, 2, 6)
    val simplified1 = WeightPresenter.simplifyRatio(parts1)
    println("  $parts1 → $simplified1")

    val parts2 = listOf(10, 5, 15)
    val simplified2 = WeightPresenter.simplifyRatio(parts2)
    println("  $parts2 → $simplified2")

    val parts3 = listOf(3, 5, 7)  // Already simplified
    val simplified3 = WeightPresenter.simplifyRatio(parts3)
    println("  $parts3 → $simplified3 (already simplified)")

    // Example 4: Percentage formatting
    println("\n" + "=".repeat(60))
    println("📊 Example 4: Percentage Formatting")
    println("-".repeat(60))

    val weights = doubleArrayOf(0.5, 0.3, 0.2)
    val percentages = WeightPresenter.formatAsPercentages(weights)
    println("  Weights: ${weights.joinToString(", ")}")
    println("  Percentages: ${percentages.joinToString(", ")}")

    // Example 5: Individual color formatting
    println("\n" + "=".repeat(60))
    println("📊 Example 5: Individual Color Formatting")
    println("-".repeat(60))

    val color = Palette.getByTitle("Yellow Ochre")!!
    val weight = 0.678

    println("  With percentage: ${WeightPresenter.formatColorWeight(color, weight, showPercentage = true)}")
    println("  With decimal:    ${WeightPresenter.formatColorWeight(color, weight, showPercentage = false)}")

    // Example 6: Real-world scenario (like your Android code)
    println("\n" + "=".repeat(60))
    println("📊 Example 6: Real-World Scenario (Android-like)")
    println("-".repeat(60))

    // Simulating: val result = optimizer.optimize(...)
    val simulatedResult = mapOf(
        "recipe" to listOf(
            Palette.getByTitle("Yellow Ochre")!! to 0.45,
            Palette.getByTitle("Vermilion Red")!! to 0.32,
            Palette.getByTitle("Ultramarine Blue")!! to 0.15,
            Palette.getByTitle("Rouge Carmine Red")!! to 0.06,
            Palette.getByTitle("Titanium White")!! to 0.02
        ),
        "finalError" to 2.34
    )

    @Suppress("UNCHECKED_CAST")
    val recipeData = simulatedResult["recipe"] as List<Pair<Color, Double>>

    val presentableRecipe = WeightPresenter.normalizeColorWeights(
        colors = recipeData,
        significanceThreshold = 0.05
    )

    if (presentableRecipe.isEmpty) {
        println("  ⚠️ No significant weights found")
    } else {
        println("  ✅ Recipe found:")
        println("  ${presentableRecipe.toFormattedString()}")
        println("\n  For UI display:")
        presentableRecipe.toDisplayList().forEach { line ->
            println("    📌 $line")
        }

        println("\n  As map:")
        presentableRecipe.toMap().forEach { (color, parts) ->
            println("    ${color.title} → $parts parts")
        }
    }

    // Example 7: Edge cases
    println("\n" + "=".repeat(60))
    println("📊 Example 7: Edge Cases")
    println("-".repeat(60))

    // All weights too small
    val tinyWeights = doubleArrayOf(0.01, 0.02, 0.01)
    val tinyPalette = Palette.favoriteColors.take(3)
    val emptyRecipe = WeightPresenter.normalizeOptimizationResult(
        tinyWeights, tinyPalette, significanceThreshold = 0.05
    )
    println("  All weights < 5%: ${emptyRecipe.isEmpty}")

    // Single color
    val singleWeight = doubleArrayOf(1.0)
    val singlePalette = listOf(Palette.getByTitle("Yellow Ochre")!!)
    val singleRecipe = WeightPresenter.normalizeOptimizationResult(
        singleWeight, singlePalette
    )
    println("  Single color: ${singleRecipe.toFormattedString()}")

    println("\n" + "=".repeat(60))
    println("✅ Weight presentation examples complete!")
    println("=".repeat(60))
}

/**
 * Example showing exact usage similar to Android code.
 */
fun androidStyleUsage() {
    println("\n📱 Android-Style Usage Example")
    println("=".repeat(60))

    // Simulating Android result
    data class MockColor(
        val title: String,
        var part: Double  // Mutable for Android compatibility
    )

    val result = mapOf(
        "recipe" to listOf(
            MockColor("Yellow Ochre", 0.45),
            MockColor("Vermilion Red", 0.32),
            MockColor("Ultramarine Blue", 0.15),
            MockColor("Rouge Carmine Red", 0.06),
            MockColor("Titanium White", 0.02)
        )
    )

    println("Before normalization:")
    @Suppress("UNCHECKED_CAST")
    (result["recipe"] as List<MockColor>).forEach { color ->
        println("  ${color.title}: ${color.part}")
    }

    // Your original code pattern:
    @Suppress("UNCHECKED_CAST")
    val significantColors = (result["recipe"] as List<MockColor>).filter { it.part > 0.05 }

    if (significantColors.isEmpty()) {
        println("\n⚠️ No significant weights found")
        // In Android: Toast.makeText(...).show()
        // In Android: recipeAdapter.submitList(emptyList())
        return
    }

    val smallest = significantColors.minOf { it.part }

    val finalColors = significantColors.mapNotNull { color ->
        val parts = (color.part / smallest).roundToInt()
        if (parts < 1) null else {
            color.part = parts.toDouble()
            color
        }
    }

    println("\n✅ After normalization:")
    finalColors.forEach { color ->
        println("  ${color.title}: ${color.part.toInt()} parts")
    }

    // Alternative using WeightPresenter (immutable, cleaner):
    println("\n💡 Using WeightPresenter (recommended):")

    val colorPairs = (result["recipe"] as List<MockColor>).map { mockColor ->
        Palette.getByTitle(mockColor.title)!! to mockColor.part
    }

    val recipe = WeightPresenter.normalizeColorWeights(colorPairs)

    if (recipe.isEmpty) {
        println("  ⚠️ No significant weights found")
    } else {
        println("  ✅ ${recipe.toFormattedString()}")
        println("\n  For adapter:")
        recipe.toDisplayList().forEach { println("    • $it") }
    }
}

fun main() {
    demonstrateWeightPresentation()
    println("\n\n")
    androidStyleUsage()
}

