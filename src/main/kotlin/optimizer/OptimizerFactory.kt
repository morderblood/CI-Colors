package optimizer

object OptimizerFactory {
    fun createOptimizer(
        algorithmName: String,
        optimizationParameters: Map<String, Any> = emptyMap()
    ): Optimizer = when (algorithmName) {
        "NSGAII" -> NSGAIIOptimizerImpl(optimizationParameters)
        "SPEA2" -> SPEA2OptimizerImpl(optimizationParameters)
        "SMSEMOA" -> SMSEMOAOptimizerImpl(optimizationParameters)
        "CMA-ES" -> CMAESOptimizerImpl(optimizationParameters)
        "Nelder-Mead" -> PowellOptimizerImpl(optimizationParameters)
        "Powell" -> PowellOptimizerImpl(optimizationParameters)
        "BOBYQA" -> BOBYQAOptimizerImpl(optimizationParameters)
        else -> throw IllegalArgumentException("Unknown algorithm: $algorithmName")
    }
}