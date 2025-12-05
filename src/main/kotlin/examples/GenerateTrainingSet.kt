package examples

import data.Palette
import evaluation.TrainingSetCreator
import functional.mixer.MixboxColorMixer

object GenerateTrainingSet {
    fun generateDataset(
        outputPath: String,
        numColors: Int = 5,
        step: Double = 0.2
    ) {

        val palette = Palette.allColors
        val mixer = MixboxColorMixer()

        val creator = TrainingSetCreator(mixer)
        val dataSet = creator.createKColorDataSet(palette, numColors, step, mixer, outputPath)
    }
}