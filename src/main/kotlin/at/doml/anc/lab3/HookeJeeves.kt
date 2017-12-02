package at.doml.anc.lab3

object HookeJeeves {

    private fun DoubleArray.norm(): Double = Math.sqrt(this.map { it * it }.sum())

    private fun DoubleArray.mul(scalar: Double): DoubleArray {
        return DoubleArray(this.size) { i -> this[i] * scalar }
    }

    private fun DoubleArray.sub(other: DoubleArray): DoubleArray {
        return DoubleArray(this.size) { i -> this[i] - other[i] }
    }

    operator fun invoke(point: DoubleArray, function: (DoubleArray) -> Double, dx: DoubleArray,
                        precision: Double): DoubleArray {
        var dx2 = dx.copyOf()
        var xp = point.copyOf()
        var xb = point.copyOf()

        do {
            val xn = search(function, xp, dx2)

            if (function(xn) < function(xb)) {
                xp = xn.mul(2.0).sub(xb)
                xb = xn
            } else {
                dx2 = dx2.mul(0.5)
                xp = xb
            }
        } while (dx2.norm() > precision)

        return xb
    }

    private fun search(function: (DoubleArray) -> Double, xp: DoubleArray, dx: DoubleArray): DoubleArray {
        val x = xp.copyOf()

        for (i in x.indices) {
            val p = function(x)
            x[i] += dx[i]
            var n = function(x)

            if (n > p) {
                x[i] -= 2 * dx[i]
                n = function(x)

                if (n > p) {
                    x[i] = x[i] + dx[i]
                }
            }
        }

        return x
    }
}
