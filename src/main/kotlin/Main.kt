import examples.SamplesGenerator

fun main() {

    val numColors = 3
    val step = 0.25
    val algo = "NSGAII"

    val outputPath = generateOutputPath(algo, numColors, step, "datasets")

    val samplesGenerator = SamplesGenerator()
    samplesGenerator.generateTrainingDataset(outputPath, numColors, step)

    samplesGenerator.generateOptimizationSamples(
        trainingDataPath = outputPath,
        resultOutputPath = generateOutputPath(algo, numColors, step, "results"),
        optimizerName = algo,
        errorName = "DeltaE2000",
        includeSparsityPenalty = true,
        useOptimizerFactoryDefaults = true,
        initialGuessType = "Uniform",
        optimizationParameters = emptyMap(),
        numberOfSamples = 10
    )
}
fun generateOutputPath(algo: String, numColors: Int, step: Double, dataType: String): String {
    return "C:\\Users\\safii\\IdeaProjects\\CI-Colors\\src\\main\\kotlin\\datasets\\$dataType-$algo-$numColors-colors-$step-step.csv"
}
