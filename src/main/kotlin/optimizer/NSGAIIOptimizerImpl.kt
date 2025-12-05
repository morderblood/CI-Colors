package optimizer

import org.moeaframework.core.TypedProperties

/**
 * Пример Optimizer на базе MOEA (NSGA-II) для одной цели.
 */
class NSGAIIOptimizerImpl(
    maxEvaluations: Int = 100,
    populationSize: Int = 100
) : MOEAOptimizerImp(maxEvaluations, populationSize) {

    override val algorithmName: String = "NSGAII"

    override val properties: TypedProperties =
        TypedProperties().apply {
            setInt("populationSize", populationSize)
            setDouble("sbx.rate", 1.0)
            setDouble("sbx.distributionIndex", 15.0)
            setDouble("pm.rate", 1.0 / populationSize)
            setDouble("pm.distributionIndex", 20.0)
        }
}
