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

        fun getDouble(name: String): Double {
            val idx = header.indexOf(name)
            return if (idx >= 0) first[idx].toDouble() else Double.NaN
        }

        fun getInt(name: String): Int {
            val idx = header.indexOf(name)
            return if (idx >= 0) first[idx].toInt() else -1
        }

        return HyperparameterSample(
            populationMultiplier = getInt("populationMultiplier"),
            sigma = getDouble("sigma"),
            diagonalOnly = getInt("diagonalOnly"),
            checkFeasibleCount = getInt("checkFeasibleCount"),
            stopFitness = getDouble("stopFitness"),
            meanError = meanError
        )
    }

    /**
     * Load all CMA-ES result samples from a folder.
     */
    fun loadAllSamples(folder: String): List<HyperparameterSample> {
        return File(folder)
            .listFiles { f -> f.name.contains("CMA-ES") }
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
