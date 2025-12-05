package functional.error

object MixingErrorFactory {
    fun createMixingError(errorName: String): MixingError {
        return when (errorName) {
            "DeltaE2000" -> DeltaE2000()
            "DeltaE76" -> DeltaE76()
            else -> throw IllegalArgumentException("Unknown mixing error: $errorName")
        }
    }
}