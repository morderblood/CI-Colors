package functional.initialGuess

class UniformInitialGuessGenerator : InitialGuessGenerator() {
    override fun generateInitialGuess(dimensions: Int) = DoubleArray(dimensions) { 1.0 / dimensions }
}