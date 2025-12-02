class SparsityPenalty (
    proportions: List<Color>,
    threshold: Double,
    penaltyPerItem: Double,
) : Penalty(proportions, threshold, penaltyPerItem) {
    override fun calculate(proportions: List<Color>): Double {
        val activeCount = proportions.count { it.part > threshold }
        return activeCount * penaltyPerItem
    }
}