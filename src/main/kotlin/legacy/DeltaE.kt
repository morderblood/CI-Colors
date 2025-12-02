class DeltaE : MixingError {
    override fun calculate(color1: Color, color2: Color) : Double{
        val dl = color1.lab.l - color2.lab.l
        val da = color1.lab.a - color2.lab.a
        val db = color1.lab.b - color2.lab.b
        return kotlin.math.sqrt(dl * dl + da * da + db * db)
    }
}