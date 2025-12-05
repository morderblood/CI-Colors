package functional.initialGuess

class RandomInitialGuessGenerator : InitialGuessGenerator() {
    override fun generateInitialGuess(dimensions: Int): DoubleArray {
        val values = DoubleArray(dimensions) { Math.random() }
        val sum = values.sum()

        for (i in values.indices) {
            values[i] /= sum
        }
        return values
    }

}