package optimizer

import org.moeaframework.core.TypedProperties

class SPEA2OptimizerImpl(
    maxEvaluations: Int = 10000,
    populationSize: Int = 20
) : MOEAOptimizerImp(maxEvaluations, populationSize) {

    override val algorithmName: String = "SPEA2"

    override val properties: TypedProperties =
        TypedProperties().apply {
            setInt("populationSize", populationSize)
            setInt("archiveSize", populationSize)
            setDouble("sbx.rate", 1.0)
            setDouble("sbx.distributionIndex", 15.0)
            setDouble("pm.rate", 1.0 / populationSize)
            setDouble("pm.distributionIndex", 20.0)
        }
}
