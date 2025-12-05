package evaluation

import java.io.File

object DataSetItemWriter {

    fun exportToCSV(dataSet: List<DataSetItem>, filePath: String) {
        File(filePath).bufferedWriter().use { writer ->
            writer.write("target_lab,target_weights\n")

            dataSet.forEach { data ->
                writer.write(data.toCSVString() + "\n")
            }
        }
    }

    fun exportSingleLineToCSV(dataSet: DataSetItem, filePath: String) {
        File(filePath).bufferedWriter().use { writer ->
            writer.append(dataSet.toCSVString())
        }
    }

    fun writeHeader(filePath: String, headers: List<String>) {
        File(filePath).bufferedWriter().use { writer ->
            writer.write(
                headers.joinToString(separator = ",") + "\n"
            )
        }
    }

    fun appendLine(item: OptimizationResultData, filePath: String) {
        File(filePath).appendText(
            item.toCSVString() + "\n"
        )
    }
}