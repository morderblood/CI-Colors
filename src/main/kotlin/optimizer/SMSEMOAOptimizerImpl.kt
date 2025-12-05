package optimizer

import org.moeaframework.core.TypedProperties

class SMSEMOAOptimizerImpl(
    maxEvaluations: Int = 10000,
    populationSize: Int = 20
) : MOEAOptimizerImp(maxEvaluations, populationSize) {

    override val algorithmName: String = "SMSEMOA"

    override val properties: TypedProperties =
        TypedProperties().apply {
            setInt("populationSize", populationSize)

            // Variation operator parameters
            setDouble("sbx.rate", 1.0)
            setDouble("sbx.distributionIndex", 20.0)
            setDouble("pm.rate", 1.0 / populationSize)
            setDouble("pm.distributionIndex", 20.0)

            // Hypervolume parameters (optional, but good practice)
            setString("hypervolume.indicator", "default")   // exact HV when possible
            setDouble("epsilon", 1e-9)                      // numerical stability
        }
}
