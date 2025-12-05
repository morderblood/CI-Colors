package optimizer

class MOEAFactory {
    fun createOptimizer(algorithmName: String): Optimizer = when (algorithmName) {
        "NSGAII" -> NSGAIIOptimizerImpl()
        "SPEA2" -> SPEA2OptimizerImpl()
        "SMSEMOA" -> SMSEMOAOptimizerImpl()
        "CMA-ES" -> CMAESOptimizerImpl()
        "Nelder-Mead" -> PowellOptimizerImpl()
        "Powell" -> PowellOptimizerImpl()
        "BOBYQA" -> BOBYQAOptimizerImpl()
        else -> throw IllegalArgumentException("Unknown algorithm: $algorithmName")
    }
}