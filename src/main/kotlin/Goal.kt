abstract class Goal (
    private val target: Color,
    private val penalities: List<Penalty>,
    private val mixingError: MixingError,
    private val normalizer: Normalizer,
    private val colorMixer: ColorMixer
) {
    public fun evaluate(solution: List<Color>): Double {
        val normalizedProportions = normalizer.normalize(solution)

        val mixedColor = colorMixer.mixColors(normalizedProportions)

        var baseError = mixingError.calculate(mixedColor, target)

        for (p in penalities) {
            baseError += (p.calculate(normalizedProportions))
        }

        return baseError
    }
}