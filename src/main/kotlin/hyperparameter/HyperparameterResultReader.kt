package hyperparameter

import domain.LabColor
import functional.error.MixingErrorFactory
import java.io.File

object HyperparameterResultReader {

    private val mixingError = MixingErrorFactory.createMixingError("DeltaE2000")

    /**
     * Load a hyperparameter sample from a single CSV file.
     * Computes mean DeltaE error from targetLab and resultLab.
     */
    fun loadSampleFromFile(filePath: String): HyperparameterSample {

        val lines = File(filePath).readLines()
        if (lines.isEmpty()) error("Empty CSV: $filePath")

        val header = lines.first().split(",")
        val rows = lines.drop(1)
        if (rows.isEmpty()) error("No data rows in file: $filePath")

        // Column positions
        val targetLabIndex = header.indexOf("targetLab")
        val resultLabIndex = header.indexOf("resultLab")
        val optimizerNameIndex = header.indexOf("optimizerName")

        if (targetLabIndex == -1 || resultLabIndex == -1)
            error("CSV missing LAB columns: $filePath")

        // ----- Compute mean DeltaE error -----
        val meanError = rows.map { row ->
            val cols = row.split(",")

            val target = parseLab(cols[targetLabIndex])
            val result = parseLab(cols[resultLabIndex])

            mixingError.calculate(result, target)
        }.average()

        val first = rows.first().split(",")

        // Extract optimizer name
        val optimizerName = if (optimizerNameIndex >= 0) {
            first[optimizerNameIndex]
        } else {
            "Unknown"
        }

        // Extract all parameters dynamically
        // Look for known parameter columns in header
        val parameterColumns = listOf(
            "populationMultiplier",
            "sigma",
            "diagonalOnly",
            "checkFeasibleCount",
            "stopFitness",
            "populationSize",
            "maxGenerations",
            "crossoverProbability",
            "mutationProbability"
        )

        val parameters = mutableMapOf<String, Any>()
        for (paramName in parameterColumns) {
            val idx = header.indexOf(paramName)
            if (idx >= 0 && idx < first.size) {
                val value = first[idx]
                // Try to parse as Int, Double, or keep as String
                parameters[paramName] = when {
                    value.toIntOrNull() != null -> value.toInt()
                    value.toDoubleOrNull() != null -> value.toDouble()
                    else -> value
                }
            }
        }

        return HyperparameterSample(
            optimizerName = optimizerName,
            parameters = parameters,
            meanError = meanError
        )
    }

    /**
     * Load all result samples from a folder.
     */
    fun loadAllSamples(folder: String): List<HyperparameterSample> {
        return File(folder)
            .listFiles { f -> f.isFile && f.extension == "csv" }
            ?.map { loadSampleFromFile(it.absolutePath) }
            ?: emptyList()
    }

    /**
     * Parse "L;a;b" into LabColor
     */
    private fun parseLab(s: String): LabColor {
        val parts = s.split(";").map { it.toDouble() }
        return LabColor(parts[0], parts[1], parts[2])
    }
}
