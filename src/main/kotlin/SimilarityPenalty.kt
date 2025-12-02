class SimilarityPenalty (
    proportions: List<Color>,
    threshold: Double,
    penaltyPerItem: Double,
) : Penalty(proportions, threshold, penaltyPerItem)  {

    private val similarityPairs = listOfNotNull(
        if (proportions.containsKey("Vermilion Red") && proportions.containsKey("Rouge Carmine Red"))
            proportions["Vermilion Red"]!! to proportions["Rouge Carmine Red"]!! else null,
        if (proportions.containsKey("Phthalo Blue") && proportions.containsKey("Prussian Blue"))
            proportions["Phthalo Blue"]!! to proportions["Prussian Blue"]!! else null,
        //if (proportions.containsKey("Titanium White") && proportions.containsKey("Black"))
        //    proportions["Titanium White"]!! to proportions["Black"]!! else null
    )

    override fun calculate(proportions: List<Color>): Double {
        var penalty = 0.0
        for ((i, j) in similarityPairs) {
            if (weights[i] > threshold && weights[j] > threshold) {
                penalty += penaltyPerPair
            }
        }
        return penalty
    }
}