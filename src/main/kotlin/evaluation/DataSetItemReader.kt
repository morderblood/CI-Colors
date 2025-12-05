package evaluation

import domain.LabColor
import java.io.File

object DataSetItemReader {

    fun readCSV(filePath: String): List<TrainingData> {
        val file = File(filePath)
        val result = mutableListOf<TrainingData>()

        file.bufferedReader().useLines { lines ->
            lines.drop(1).forEach { line ->  // Skip header
                if (line.isBlank()) return@forEach
                result.add(parseLine(line))
            }
        }

        return result
    }

    fun readSingleLine(filePath: String): TrainingData {
        val line = File(filePath).readText().trim()
        return parseLine(line)
    }

    private fun parseLine(line: String): TrainingData {
        // Expected format:
        // L;A;B,w1;w2;w3;...

        val parts = line.split(",", limit = 2)
        if (parts.size != 2)
            throw IllegalArgumentException("Invalid CSV format: $line")

        val labString = parts[0]
        val weightString = parts[1]

        // LAB values remain full precision
        val labValues = labString.split(";").map { it.toDouble() }
        if (labValues.size != 3)
            throw IllegalArgumentException("Expected 3 LAB values, got: $labValues")

        val lab = LabColor(
            l = labValues[0],
            a = labValues[1],
            b = labValues[2]
        )

        // Weights use 0.00 format, parse as double
        val weights = weightString
            .split(";")
            .map { it.toDouble() }
            .toDoubleArray()

        return TrainingData(
            targetLab = lab,
            weights = weights
        )
    }
}

