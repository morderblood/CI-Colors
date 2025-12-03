package data

import domain.Color
import domain.LabColor

/**
 * Pre-defined color palette with common pigments.
 *
 * This palette contains professional-grade pigment colors commonly used
 * in painting and color mixing applications.
 */
object Palette {

    /**
     * Complete palette of all available colors.
     */
    val allColors = listOf(
        Color(
            id = 1,
            title = "Yellow Ochre",
            imageName = "c_yellow_ochre",
            hex = "#CC7722",
            rgb = listOf(203, 157, 6),
            lab = LabColor(l = 67.139, a = 8.734, b = 70.894),
            isFavorite = true
        ),
        Color(
            id = 2,
            title = "Vermilion Red",
            imageName = "c_red",
            hex = "#FF0000",
            rgb = listOf(227, 66, 52),
            lab = LabColor(l = 52.411, a = 61.319, b = 44.474),
            isFavorite = true
        ),
        Color(
            id = 3,
            title = "Lemon Yellow",
            imageName = "c_lemon",
            hex = "#FFF44F",
            rgb = listOf(255, 244, 79),
            lab = LabColor(l = 94.217, a = -7.812, b = 89.702),
            isFavorite = false
        ),
        Color(
            id = 4,
            title = "Ultramarine Blue",
            imageName = "c_ultramarine",
            hex = "#3F00FF",
            rgb = listOf(18, 10, 143),
            lab = LabColor(l = 32.108, a = 55.441, b = -81.671),
            isFavorite = true
        ),
        Color(
            id = 5,
            title = "Rouge Carmine Red",
            imageName = "c_davinci_red",
            hex = "#C41E3A",
            rgb = listOf(155, 35, 33),
            lab = LabColor(l = 40.368, a = 54.982, b = 33.108),
            isFavorite = true
        ),
        Color(
            id = 6,
            title = "Brown Ochre",
            imageName = "c_brown_ochre",
            hex = "#8A4B16",
            rgb = listOf(138, 75, 22),
            lab = LabColor(l = 45.739, a = 19.935, b = 46.822),
            isFavorite = false
        ),
        Color(
            id = 7,
            title = "Transparent Orange",
            imageName = "c_transperent_orange",
            hex = "#FF7F00",
            rgb = listOf(255, 127, 0),
            lab = LabColor(l = 70.709, a = 49.811, b = 79.531),
            isFavorite = false
        ),
        Color(
            id = 8,
            title = "Titanium White",
            imageName = "c_white",
            hex = "#FFFFFF",
            rgb = listOf(255, 255, 255),
            lab = LabColor(l = 100.0, a = 0.005, b = -0.010),
            isFavorite = true
        ),
        Color(
            id = 9,
            title = "Phthalo Blue",
            imageName = "c_phthalo",
            hex = "#000f89",
            rgb = listOf(0, 15, 137),
            lab = LabColor(l = 16.243, a = 44.354, b = -64.942),
            isFavorite = false
        ),
        Color(
            id = 10,
            title = "Black",
            imageName = "c_black",
            hex = "#000000",
            rgb = listOf(0, 0, 0),
            lab = LabColor(l = 0.0, a = 0.0, b = 0.0),
            isFavorite = true
        ),
        Color(
            id = 11,
            title = "Prussian Blue",
            imageName = "c_prussian",
            hex = "#003153",
            rgb = listOf(0, 49, 83),
            lab = LabColor(l = 19.3131, a = -0.4149, b = -24.8864),
            isFavorite = false
        )
    )

    /**
     * Palette containing only favorite colors.
     */
    val favoriteColors = allColors.filter { it.isFavorite }

    /**
     * Get color by title.
     */
    fun getByTitle(title: String): Color? {
        return allColors.find { it.title.equals(title, ignoreCase = true) }
    }

    /**
     * Get color by ID.
     */
    fun getById(id: Int): Color? {
        return allColors.find { it.id == id }
    }

    /**
     * Get similarity pairs for penalty calculation.
     * Returns pairs of color indices that should not be used together.
     */
    fun getSimilarityPairs(): List<Pair<Int, Int>> {
        val indexMap = allColors.withIndex().associate { it.value.title to it.index }

        return listOfNotNull(
            // Vermilion Red and Rouge Carmine Red are similar
            indexMap["Vermilion Red"]?.let { i ->
                indexMap["Rouge Carmine Red"]?.let { j -> i to j }
            },
            // Phthalo Blue and Prussian Blue are similar
            indexMap["Phthalo Blue"]?.let { i ->
                indexMap["Prussian Blue"]?.let { j -> i to j }
            }
        )
    }

    /**
     * Color groups by type.
     */
    object Groups {
        val yellows = allColors.filter {
            it.title.contains("Yellow", ignoreCase = true) ||
            it.title.contains("Ochre", ignoreCase = true)
        }

        val reds = allColors.filter {
            it.title.contains("Red", ignoreCase = true) ||
            it.title.contains("Orange", ignoreCase = true)
        }

        val blues = allColors.filter {
            it.title.contains("Blue", ignoreCase = true)
        }

        val neutrals = allColors.filter {
            it.title in listOf("Titanium White", "Black", "Brown Ochre")
        }
    }
}

