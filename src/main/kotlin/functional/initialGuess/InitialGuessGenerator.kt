package functional.initialGuess

/**
 * Abstract class for initial guess generators.
 */
abstract class InitialGuessGenerator () {
    abstract fun generateInitialGuess(dimensions: Int): DoubleArray
}
