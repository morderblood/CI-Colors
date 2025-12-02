class ProportionsNormalizer: Normalizer {
    override fun normalize(proportions: List<Color>): List<Color> {
        val clipped = proportions.map { color ->
            if (color.part < 0.0) color.part = 0.0
            color
        }
        val sum = clipped.sumOf { it.part }

        if (sum == 0.0) {
            // если всё обнулилось — делаем равномерное распределение
            proportions.forEach { color -> color.part = 1.0 / sum }
            return proportions
        } else {
            clipped.forEach { color -> color.part /= sum }
            return clipped
        }
    }
}