package legacy

// TODO: This file is superseded by penalty.Penalty interface and its implementations
// Use penalty.SparsityPenalty and penalty.SimilarityPenalty instead

class Penalties {
    companion object {
        /**
         * Считаем штраф за одновременное использование похожих цветов.
         * similarityPairs: пары индексов (i, j), которые нельзя использовать одновременно.
         *
         * Penaltiy for simultanious usage of similar colors.
         * similarityPairs: pairs of indices (i, j), which cannot be used simultaneously.
         */
        fun similarityPenalty(
            weights: DoubleArray,
            similarityPairs: List<Pair<Int, Int>>,
            threshold: Double = 0.1,      // min part for a color to be treated as used
            penaltyPerPair: Double = 1.0  // penalty for each pair
        ): Double {
            var penalty = 0.0
            for ((i, j) in similarityPairs) {
                if (weights[i] > threshold && weights[j] > threshold) {
                    penalty += penaltyPerPair
                }
            }
            return penalty
        }

        /**
         * Штраф за количество активных пигментов (стремимся использовать меньше).
         *
         * Penalty for the high number of active pigments (we want to use less).
         */
        fun sparsityPenalty(
            weights: DoubleArray,
            threshold: Double = 0.01,     // min part for a color to be treated as used
            penaltyPerPigment: Double = 1.0
        ): Double {
            val activeCount = weights.count { it > threshold }
            return activeCount * penaltyPerPigment
        }
    }

}