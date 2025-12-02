data class Color(
    var id: Int? = null,
    var title: String,
    var imageName: String,
    var hex: String,
    var rgb: List<Int>? = null,
    var lab: LabColor? = null,
    var isFavorite: Boolean,
    var part: Double = 1.0
) {
    companion object
}