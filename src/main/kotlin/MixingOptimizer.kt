class MixtureOptimizer {


    fun optimizeMixture(
        targetHEX: String,
        colors: List<Color>
    ): Map<String, Any> {

        val baseColors = colors.map { it.lab!! }

        val target = LabColor.fromHex(targetHEX)

        val indexMap = colors.mapIndexed { newIndex, color ->
            color.title to newIndex
        }.toMap()

        val similarityPairs = listOfNotNull(
            if (indexMap.containsKey("Vermilion Red") && indexMap.containsKey("Rouge Carmine Red"))
                indexMap["Vermilion Red"]!! to indexMap["Rouge Carmine Red"]!! else null,
            if (indexMap.containsKey("Phthalo Blue") && indexMap.containsKey("Prussian Blue"))
                indexMap["Phthalo Blue"]!! to indexMap["Prussian Blue"]!! else null,
            //if (indexMap.containsKey("Titanium White") && indexMap.containsKey("Black"))
            //    indexMap["Titanium White"]!! to indexMap["Black"]!! else null
        )


        val objective = ColorMixObjective(
            baseColors = baseColors,
            target = target,
            similarityPairs = similarityPairs,
            lambdaSimilar = 3.0,
            lambdaSparse = 3.0
        )

        val dim = baseColors.size

        if (dim == 0) {
            throw IllegalArgumentException("No base colors provided")
        }

        // Find the color the most similar to the target as initial guess
        val closestIndex = baseColors.indices.minByOrNull { i ->
            deltaE(baseColors[i], target)
        } ?: 0
        val initialGuess = DoubleArray(dim) { i ->
            if (i == closestIndex) 0.8 else 0.01
        }        /**
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

        //val initialGuess = DoubleArray(dim) { 1.0 / dim }

        val result = findBestOptimization(
            objective = objective,
            dim = baseColors.size,
            initialGuess = initialGuess
        )

        val optimalRaw = result.second.point
        val optimalWeights = normalizeWeights(optimalRaw)

        val mixed = mixColors(optimalWeights, baseColors)
        val finalError = deltaE(mixed, target)

        optimalWeights.forEachIndexed { index, weight ->
            colors[index].part = weight
        }

        return mapOf(
            "recipe" to colors,
            "mixedColor" to mixed,
            "finalError" to "%.3f".format(finalError),
            "modelName" to result.first
        )
    }

    fun findBestOptimization(
        objective: MultivariateFunction,
        dim: Int,
        initialGuess: DoubleArray
    ): Pair<String, PointValuePair> {

        var result: Triple<String, PointValuePair, Exception?>
        run {
            val optimizer = CMAESOptimizer(
                10000,
                1e-9,
                true,
                0,
                0,
                MersenneTwister(),
                false,
                null,
            )
            val r = optimizer.optimize(
                MaxEval(50000),
                ObjectiveFunction(objective),
                GoalType.MINIMIZE,
                InitialGuess(initialGuess),
                CMAESOptimizer.Sigma(DoubleArray(dim) { 0.2 }),
                CMAESOptimizer.PopulationSize(5 * dim),
                SimpleBounds(DoubleArray(dim) { 0.0 }, DoubleArray(dim) { 1.0 })
            )
            result = Triple("CMA-ES", r, null)
        }

        run {
            try {
                val simplex = NelderMeadSimplex(DoubleArray(dim) { 0.2 })
                val optimizer = SimplexOptimizer(1e-6, 1e-6)
                val r = optimizer.optimize(
                    ObjectiveFunction(objective),
                    GoalType.MINIMIZE,
                    InitialGuess(result.second.point),
                    simplex,
                    MaxEval(50000)
                )
                result = Triple("NelderMead", r, null)
            } catch (e: Exception) {
                result = Triple("NelderMead", result.second, e)
            }
        }

        return result.first to result.second
    }


    companion object {
        /*fun deltaE(c1: LabColor, c2: LabColor): Double {
            val dl = c1.l - c2.l
            val da = c1.a - c2.a
            val db = c1.b - c2.b
            return kotlin.math.sqrt(dl * dl + da * da + db * db)
        }*/

        /**
         * Смешиваем список Lab-цветов с заданными весами (предполагаем, что веса уже нормированы).
         */
        fun mixColors(weights: DoubleArray, baseColors: List<LabColor>): LabColor {
            if (weights.size != baseColors.size || weights.isEmpty()) {
                // Return a neutral color or throw an error if inputs are invalid
                return LabColor.fromRgb(128, 128, 128)
            }

            // Create a list of colors and their weights, filtering out those with zero weight
            val weightedColors = weights.mapIndexed { index, weight -> baseColors[index].toInt() to weight }
                .filter { it.second > 1e-6 } // Ignore colors with negligible contribution
                .sortedByDescending { it.second } // Sort by weight, descending

            if (weightedColors.isEmpty()) {
                return LabColor.fromRgb(128, 128, 128) // Or handle as an edge case
            }
            if (weightedColors.size == 1) {
                // If only one color has weight, return it directly
                val hex = String.format("#%06X", 0xFFFFFF and weightedColors[0].first)
                return LabColor.fromHex(hex)
            }

            // Start with the color that has the highest weight
            var mixedColor = weightedColors[0].first
            var totalWeight = weightedColors[0].second

            // Iteratively mix the remaining colors
            for (i in 1 until weightedColors.size) {
                val nextColor = weightedColors[i].first
                val nextWeight = weightedColors[i].second

                // The mixing ratio `t` is the proportion of the next color in the new mixture
                val t = nextWeight / (totalWeight + nextWeight)

                // 3. Use the official Mixbox.lerp function
                // Note: Mixbox.lerp expects a Float, so we cast 't'
                mixedColor = Mixbox.lerp(mixedColor, nextColor, t.toFloat())
                totalWeight += nextWeight
            }

            // The final result `mixedColor` is an ARGB integer. Convert it back to LabColor.
            val hex = String.format("#%06X", 0xFFFFFF and mixedColor)
            return LabColor.fromHex(hex)
        }


        fun deltaE(
            c1: LabColor,
            c2: LabColor,
            kL: Double = 0.8,
            kC: Double = 1.0,
            kH: Double = 0.5
        ): Double {

            val (L1, a1, b1) = c1
            val (L2, a2, b2) = c2

            // Step 1: Chroma for each color
            val c1Val = sqrt(a1 * a1 + b1 * b1)
            val c2Val = sqrt(a2 * a2 + b2 * b2)

            // Step 2: Average chroma
            val cBar = (c1Val + c2Val) / 2.0

            // Step 3: G factor
            val cBar7 = cBar.pow(7.0)
            val g = 0.5 * (1.0 - sqrt(cBar7 / (cBar7 + 25.0.pow(7.0))))

            // Step 4: a' values
            val a1Prime = (1.0 + g) * a1
            val a2Prime = (1.0 + g) * a2

            // Step 5: C' values
            val c1Prime = sqrt(a1Prime * a1Prime + b1 * b1)
            val c2Prime = sqrt(a2Prime * a2Prime + b2 * b2)
            val cBarPrime = (c1Prime + c2Prime) / 2.0

            // Step 6: h' values (degrees)
            val h1Prime = hueAngleDegrees(a1Prime, b1)
            val h2Prime = hueAngleDegrees(a2Prime, b2)

            // Step 7: ΔL', ΔC', ΔH'
            val deltaLPrime = L2 - L1
            val deltaCPrime = c2Prime - c1Prime

            val deltaHPrime = if (c1Prime * c2Prime == 0.0) {
                0.0
            } else {
                var dh = h2Prime - h1Prime
                if (dh > 180.0) dh -= 360.0
                if (dh < -180.0) dh += 360.0
                2.0 * sqrt(c1Prime * c2Prime) * sinDeg(dh / 2.0)
            }

            // Step 8: Means
            val LBarPrime = (L1 + L2) / 2.0

            val hBarPrime = if (c1Prime * c2Prime == 0.0) {
                h1Prime + h2Prime
            } else {
                val diff = abs(h1Prime - h2Prime)
                when {
                    diff > 180.0 && (h1Prime + h2Prime) < 360.0 -> (h1Prime + h2Prime + 360.0) / 2.0
                    diff > 180.0 -> (h1Prime + h2Prime - 360.0) / 2.0
                    else -> (h1Prime + h2Prime) / 2.0
                }
            }

            // Step 9: T factor
            val t = 1.0 -
                    0.17 * cosDeg(hBarPrime - 30.0) +
                    0.24 * cosDeg(2.0 * hBarPrime) +
                    0.32 * cosDeg(3.0 * hBarPrime + 6.0) -
                    0.20 * cosDeg(4.0 * hBarPrime - 63.0)

            // Step 10: Sl, Sc, Sh
            val sL = 1.0 + (0.015 * (LBarPrime - 50.0).pow(2.0)) /
                    sqrt(20.0 + (LBarPrime - 50.0).pow(2.0))

            val sC = 1.0 + 0.045 * cBarPrime
            val sH = 1.0 + 0.015 * cBarPrime * t

            // Step 11: Δθ, Rc, Rt
            val deltaTheta = 30.0 * exp(-((hBarPrime - 275.0) / 25.0).pow(2.0))
            val rC = 2.0 * sqrt(cBarPrime.pow(7.0) / (cBarPrime.pow(7.0) + 25.0.pow(7.0)))
            val rT = -sinDeg(2.0 * deltaTheta) * rC

            // Final ΔE2000
            val lTerm = deltaLPrime / (kL * sL)
            val cTerm = deltaCPrime / (kC * sC)
            val hTerm = deltaHPrime / (kH * sH)

            return sqrt(
                lTerm * lTerm +
                        cTerm * cTerm +
                        hTerm * hTerm +
                        rT * cTerm * hTerm
            )
        }

        /** Hue angle in degrees (0–360). */
        private fun hueAngleDegrees(a: Double, b: Double): Double {
            if (a == 0.0 && b == 0.0) return 0.0
            var angle = Math.toDegrees(atan2(b, a))
            if (angle < 0.0) angle += 360.0
            return angle
        }

        private fun sinDeg(deg: Double): Double = sin(Math.toRadians(deg))
        private fun cosDeg(deg: Double): Double = cos(Math.toRadians(deg))


        /**
         * Делаем веса неотрицательными и нормируем их так, чтобы сумма была 1.
         */
        fun normalizeWeights(raw: DoubleArray): DoubleArray {
            val clipped = raw.map { w ->
                if (w < 0.0) 0.0 else w
            }
            val sum = clipped.sum()
            return if (sum == 0.0) {
                // если всё обнулилось — делаем равномерное распределение
                DoubleArray(raw.size) { 1.0 / raw.size }
            } else {
                clipped.map { it / sum }.toDoubleArray()
            }
        }
    }
}

