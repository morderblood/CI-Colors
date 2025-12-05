package evaluation

import domain.Color
import functional.mixer.ColorMixer
import java.io.File
import java.util.Locale
import kotlin.random.Random

class TrainingSetCreator(
    private val mixer: ColorMixer
) {

    /**
     * Creates a dataset of all possible color combinations.
     */
    fun createDataSet(palette: List<Color>): List<TrainingData> {
        val dataSet = mutableListOf<TrainingData>()

        dataSet.addAll(createPairwiseDataSet(palette))

        return dataSet
    }

    /**
     * Creates a dataset of all two color combinations in the palette.
     */
    private fun createPairwiseDataSet(palette: List<Color>): List<TrainingData> {
        val dataSet = mutableListOf<TrainingData>()

        for (i in palette.indices) {
            for (j in palette.indices) {
                if (i == j) {
                    continue
                }
                val color1 = palette[i]
                val color2 = palette[j]
                for (k in 1..99) {
                    val weights = DoubleArray(palette.size) {0.0}.apply {
                        this[i] = k / 100.0
                        this[j] = 1.0 - (k / 100.0)
                    }
                    val mixedColor = mixer.mixColors(weights, palette)
                    val trainingData = TrainingData(
                        targetLab = mixedColor,
                        weights = weights
                    )
                    dataSet.add(trainingData)
                }
            }
        }
        return dataSet
    }

    private fun <T> combinations(list: List<T>, k: Int): List<List<T>> {
        val result = mutableListOf<List<T>>()

        fun recurse(start: Int, current: MutableList<T>) {
            if (current.size == k) {
                result.add(current.toList())
                return
            }
            for (i in start until list.size) {
                current.add(list[i])
                recurse(i + 1, current)
                current.removeAt(current.size - 1)
            }
        }

        recurse(0, mutableListOf())
        return result
    }

    private fun generateWeightCombinations(k: Int, step: Double = 0.01): List<DoubleArray> {
        val results = mutableListOf<DoubleArray>()

        fun recurse(index: Int, remaining: Double, current: DoubleArray) {
            if (index == k - 1) {
                current[index] = remaining
                results.add(current.copyOf())
                return
            }

            var w = 0.0
            while (w <= remaining) {
                current[index] = w
                recurse(index + 1, remaining - w, current)
                w = (w + step).roundToDecimals(4)
            }
        }

        recurse(0, 1.0, DoubleArray(k))
        return results
    }

    private fun Double.roundToDecimals(decimals: Int): Double =
        "%.4f".format(Locale.US, this).toDouble()

    fun createKColorDataSet(
        palette: List<Color>,
        k: Int,
        step: Double = 0.01,
        mixer: ColorMixer,
        outputPath: String
    ): Long {

        val combos = combinations(palette, k)
        val weightSets = generateWeightCombinations(k, step)

        val file = File(outputPath)
        file.bufferedWriter().use { writer ->

            // Write header ONCE
            writer.write("target_lab,target_weights\n")

            var count = 0L

            for (combo in combos) {
                val indices = combo.map { palette.indexOf(it) }

                for (w in weightSets) {
                    val fullWeights = DoubleArray(palette.size)

                    for ((i, paletteIndex) in indices.withIndex()) {
                        fullWeights[paletteIndex] = w[i]
                    }

                    val mixedColor = mixer.mixColors(fullWeights, palette)

                    val td = TrainingData(mixedColor, fullWeights)
                    writer.write(td.toCSVString())
                    writer.newLine()

                    count++
                }
            }

            return count
        }
    }

    fun createRandom3ColorDataSet(
        palette: List<Color>,
        mixer: ColorMixer,
        outputPath: String,
        maxItems: Int = 100
    ): Long {

        val k = 3   // always pick 3 colors
        val combos = combinations(palette, k)

        val random = Random(System.currentTimeMillis())

        val file = File(outputPath)
        var count = 0L

        file.bufferedWriter().use { writer ->

            writer.write("target_lab,target_weights\n")

            loop@ for (combo in combos) {
                val indices = combo.map { palette.indexOf(it) }

                // Generate many random samples, but stop at 100 total items
                repeat(10_000) {  // upper bound, loop exits early via break@loop
                    if (count >= maxItems) break@loop

                    // ---- RANDOM WEIGHTS THAT SUM TO 1 ----
                    val raw = DoubleArray(k) { random.nextDouble() }
                    val sum = raw.sum()
                    val weights = raw.map { it / sum }  // normalized

                    // place weights into full palette-size array
                    val fullWeights = DoubleArray(palette.size)
                    for (i in 0 until k) {
                        fullWeights[indices[i]] = weights[i]
                    }

                    // mix color
                    val mixedColor = mixer.mixColors(fullWeights, palette)

                    val td = TrainingData(mixedColor, fullWeights)
                    writer.write(td.toCSVString())
                    writer.newLine()

                    count++
                }
            }
        }

        return count
    }

}