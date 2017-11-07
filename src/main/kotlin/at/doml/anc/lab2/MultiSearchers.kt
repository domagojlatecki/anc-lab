package at.doml.anc.lab2

import java.lang.Math.sqrt

object MultiSearchers {

    private operator fun DoubleArray.minus(other: DoubleArray): DoubleArray {
        val new = DoubleArray(this.size) { i -> this[i] }

        for (i in this.indices) {
            new[i] -= other[i]
        }

        return new
    }

    private fun DoubleArray.norm(): Double = Math.sqrt(this.map { it * it }.sum())

    private fun DoubleArray.add(other: DoubleArray): DoubleArray {
        val new = DoubleArray(this.size) { i -> this[i] }

        for (i in this.indices) {
            new[i] += other[i]
        }

        return new
    }

    private fun DoubleArray.add(value: Double): DoubleArray {
        val new = DoubleArray(this.size) { i -> this[i] }

        for (i in this.indices) {
            new[i] += value
        }

        return new
    }

    private operator fun Double.times(array: DoubleArray): DoubleArray {
        val new = DoubleArray(array.size) { i -> array[i] }

        for (i in array.indices) {
            new[i] *= this
        }

        return new
    }

    fun coordinateSearch(initialPoint: DoubleArray, fn: CountableFunction<DoubleArray>,
                         precision: Double, h: Double, epsilon: DoubleArray, verbose: Boolean): DoubleArray {
        var x = initialPoint.copyOf()
        var xs = x

        while ((x - xs).norm() <= precision) {
            xs = x.copyOf()

            for (i in initialPoint.indices) {
                val prevCounts = fn.count
                val aFn: CountableFunction<Double> = CountableFunction { a -> fn(x.add(a * epsilon)) }
                val (start, end) = Algorithms.unimodalInterval(h, 0.0, aFn, verbose)
                val alphaMin = Algorithms.goldenSectionSearch(start, end, aFn, precision, verbose)
                x = x.add(alphaMin * epsilon[i])
                fn.count = aFn.count + prevCounts
            }
        }

        return x
    }

    private fun calculateSimplexPoints(initialPoint: DoubleArray, simplexOffset: Double): Array<DoubleArray> {
        val points = Array(initialPoint.size) { initialPoint.copyOf() }

        for (i in points.indices) {
            points[i][i] += simplexOffset
        }

        return points + initialPoint.copyOf()
    }

    private fun List<Double>.argMax(): Int {
        var max = this[0]
        var arg = 0

        for (i in this.indices) {
            if (this[i] >= max) {
                max = this[i]
                arg = i
            }
        }

        return arg
    }

    private fun List<Double>.argMin(): Int {
        var min = this[0]
        var arg = 0

        for (i in this.indices) {
            if (this[i] <= min) {
                min = this[i]
                arg = i
            }
        }

        return arg
    }

    private fun Array<DoubleArray>.centroid(h: Int): DoubleArray {
        val k = this.size - 1
        val sums = this.filterIndexed { index, _ -> index != h }.reduce { f, s -> f.add(s) }
        return sums.map { it / k }.toDoubleArray()
    }

    private fun List<Double>.greaterThanAllExcept(index: Int, value: Double): Boolean {
        return this.filterIndexed { i, _ -> i != index }.all { it < value }
    }

    private fun sumOfSquareDifferences(list: List<Double>, value: Double): Double {
        return list.map { (it - value) * (it - value) }.sum()
    }

    private fun Array<DoubleArray>.moveAllTowards(index: Int) {
        val value = this[index]

        for (i in this.indices) {
            if (i != index) {
                this[i] = 0.5 * (this[i] - value)
            }
        }
    }

    private fun DoubleArray.prettyString(): String {
        return this.joinToString(
                prefix = "{",
                separator = ", ",
                postfix = "}"
        )
    }

    private fun printCentroid(xc: DoubleArray, fn: CountableFunction<DoubleArray>) {
        println("┌")
        println("│ xₒ = ${xc.prettyString()}")
        println("│ f(xₒ) = ${fn.invokeWithoutCount(xc)}")
        println("└")
    }

    fun nelderMeadSimplexSearch(initialPoint: DoubleArray, alpha: Double,
                                beta: Double, gamma: Double, sigma: Double, precision: Double,
                                fn: CountableFunction<DoubleArray>, verbose: Boolean): DoubleArray {
        val x = calculateSimplexPoints(initialPoint, sigma)
        var xc: DoubleArray
        var fnX = x.map { fn(it) }
        var fXc: Double

        do {
            val h = fnX.argMax()
            val l = fnX.argMin()

            xc = x.centroid(h)

            if (verbose) {
                printCentroid(xc, fn)
            }

            val xr = (1.0 + alpha) * xc - alpha * x[h]
            val fXr = fn(xr)

            if (fXr < fnX[l]) {
                val xe = ((1.0 - gamma) * xc).add(gamma * xr)

                if (fn(xe) < fnX[l]) {
                    x[h] = xe
                } else {
                    x[h] = xr
                }
            } else if (fnX.greaterThanAllExcept(h, fXr)) {
                if (fXr < fnX[h]) {
                    x[h] = xr

                    val xk = ((1.0 - beta) * xc).add(beta * x[h])

                    if (fn(xk) < fnX[h]) {
                        x[h] = xk
                    } else {
                        x.moveAllTowards(l)
                    }
                }
            } else {
                x[h] = xr
            }

            fnX = x.map { fn(it) }
            fXc = fn(xc)
        } while (sqrt(sumOfSquareDifferences(fnX, fXc) / x.size) < precision)

        return xc
    }


    private fun search(xp: DoubleArray, dx: DoubleArray, fn: CountableFunction<DoubleArray>): DoubleArray {
        val x = xp.copyOf()

        for (i in x.indices) {
            val p = fn(x)
            x[i] += dx[i]
            var n = fn(x)

            if (n > p) {
                x[i] -= 2 * dx[i]
                n = fn(x)

                if (n > p) {
                    x[i] = x[i] + dx[i]
                }
            }
        }

        return x
    }

    private fun printPoints(xp: DoubleArray, xb: DoubleArray, xn: DoubleArray, fn: CountableFunction<DoubleArray>) {
        println("┌")
        println("│ xₚ = ${xp.prettyString()}, xₛ = ${xb.prettyString()}, xₙ = ${xn.prettyString()}")
        println("│ f(xₚ) = ${fn.invokeWithoutCount(xp)}, " +
                "f(xₛ) = ${fn.invokeWithoutCount(xb)}, " +
                "f(xₙ) = ${fn.invokeWithoutCount(xn)}")
        println("└")
    }

    fun hookeJeevesSearch(initialPoint: DoubleArray, dx: DoubleArray, precision: Double,
                          fn: CountableFunction<DoubleArray>, verbose: Boolean): DoubleArray {
        var dx2 = dx
        var xp = initialPoint.copyOf()
        var xb = initialPoint.copyOf()

        do {
            val xn = search(xp, dx2, fn)

            if (verbose) {
                printPoints(xp, xb, xn, fn)
            }

            if (fn(xn) < fn(xb)) {
                xp = 2.0 * xn - xb
                xb = xn
            } else {
                dx2 = 0.5 * dx2
                xp = xb
            }
        } while (dx2.norm() > precision)

        return xb
    }
}
