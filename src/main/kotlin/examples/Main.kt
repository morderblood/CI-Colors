package examples

class Main {
    fun main() {

        val numColors = 3
        val step = 0.25
        val algo = "NSGAII"

        val outputPath = generateOutputPath(algo, numColors, step, "results")

        val samplesGenerator = SamplesGenerator()
        samplesGenerator.generateTrainingDataset(outputPath, numColors, step)


    }
    fun generateOutputPath(algo: String, numColors: Int, step: Double, dataType: String): String {
        return "C:\\Users\\safii\\IdeaProjects\\CI-Colors\\src\\main\\kotlin\\datasets\\$dataType-$algo-$numColors-colors-$step-step.csv"
    }
}