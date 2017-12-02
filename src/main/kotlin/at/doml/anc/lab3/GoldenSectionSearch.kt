package at.doml.anc.lab3

object GoldenSectionSearch {

    private val goldenRatio: Double = 0.5 * (Math.sqrt(5.0) - 1)

    operator fun invoke(start: Double, end: Double, fn: (Double) -> Double, precision: Double): Double {
        var a = start
        var b = end
        var c = b - (b - a) * goldenRatio
        var d = a + (b - a) * goldenRatio
        var fc = fn(c)
        var fd = fn(d)

        while ((b - a) > precision) {
            if (fc < fd) {
                b = d
                d = c
                c = b - (b - a) * goldenRatio
                fd = fc
                fc = fn(c)
            } else {
                a = c
                c = d
                d = a + (b - a) * goldenRatio
                fc = fd
                fd = fn(d)
            }
        }

        return (a + b) / 2.0
    }
}
