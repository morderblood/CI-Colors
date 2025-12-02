package domain

/**
 * Domain Layer: Immutable representation of a pigment/color.
 * Contains only static facts about the color - no optimization state.
 *
 * Принцип: Доменные объекты никогда не изменяются во время оптимизации.
 */
data class Color(
    val id: Int? = null,
    val title: String,
    val imageName: String,
    val hex: String,
    val rgb: List<Int>? = null,
    val lab: LabColor,
    val isFavorite: Boolean = false
) {
    companion object
}
