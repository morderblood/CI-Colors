package optimizer

import org.moeaframework.core.TypedProperties

class SMSEMOAOptimizerImpl(
    optimizationParameters: Map<String, Any> = emptyMap()
) : MOEAOptimizerImp(optimizationParameters) {

    override val algorithmName: String = "SMSEMOA"

    override val properties: TypedProperties =
        TypedProperties().apply {
            setInt("populationSize", optimizationParameters["populationSize"] as? Int ?: 100)

            setDouble("sbx.rate", optimizationParameters["sbxRate"] as? Double ?: 1.0)
            setDouble("sbx.distributionIndex", optimizationParameters["sbxDistributionIndex"] as? Double ?: 15.0)
            setDouble("pm.rate", 1.0 / (optimizationParameters["populationSize"] as? Int ?: 100))
            setDouble("pm.distributionIndex", optimizationParameters["pmDistributionIndex"] as? Double ?: 20.0)

            // Hypervolume parameters (optional, but good practice)
            setString("hypervolume.indicator", "default")   // exact HV when possible
            setDouble("epsilon", 1e-9)                      // numerical stability
        }
}
