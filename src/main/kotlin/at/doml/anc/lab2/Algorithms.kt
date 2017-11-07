package at.doml.anc.lab2

object Algorithms {

    private val goldenRatio: Double = 0.5 * (Math.sqrt(5.0) - 1)

    fun goldenSectionSearch(start: Double, end: Double, fn: CountableFunction<Double>,
                            precision: Double, verbose: Boolean): Double {
        var a = start
        var b = end
        var c = b - (b - a) * goldenRatio
        var d = a + (b - a) * goldenRatio
        var eval = 0
        var fc = fn(c); eval += 1
        var fd = fn(d); eval += 1

        if (verbose) {
            printPoints(a, b, c, d, fn)
        }

        while ((b - a) > precision) {
            if (fc < fd) {
                b = d
                d = c
                c = b - (b - a) * goldenRatio
                fd = fc
                fc = fn(c); eval += 1
            } else {
                a = c
                c = d
                d = a + (b - a) * goldenRatio
                fc = fd
                fd = fn(d); eval += 1
            }

            if (verbose) {
                printPoints(a, b, c, d, fn)
            }
        }

        return (a + b) / 2.0
    }

    private fun printPoints(a: Double, b: Double, c: Double, d: Double, fn: CountableFunction<Double>) {
        println("┌")
        println("│ a = $a, b = $b, c = $c, d = $d")
        println("│ f(a) = ${fn.invokeWithoutCount(a)}, " +
                "f(b) = ${fn.invokeWithoutCount(b)}, " +
                "f(c) = ${fn.invokeWithoutCount(c)}, " +
                "f(d) = ${fn.invokeWithoutCount(d)}")
        println("└")
    }

    fun unimodalInterval(h: Double, point: Double, fn: CountableFunction<Double>,
                         verbose: Boolean): Pair<Double, Double> {
        var l = point - h
        var r = point + h
        var m = point
        var step = 1
        var eval = 0
        var fm = fn(point); eval += 1
        var fl = fn(l); eval += 1
        var fr = fn(r); eval += 1

        if (verbose) {
            printPoints(m, l, r, fn)
        }

        if (fm < fr && fm < fl) {
            // do nothing
        } else if (fm > fr) {
            do {
                l = m
                m = r
                fm = fr
                step *= 2
                r = point + h * step
                fr = fn(r); eval += 1

                if (verbose) {
                    printPoints(m, l, r, fn)
                }
            } while (fm > fr)
        } else {
            do {
                r = m
                m = l
                fm = fl
                step *= 2
                l = point - h * step
                fl = fn(l); eval += 1

                if (verbose) {
                    printPoints(m, l, r, fn)
                }
            } while (fm > fl)
        }

        return Pair(l, r)
    }

    private fun printPoints(m: Double, l: Double, r: Double, fn: CountableFunction<Double>) {
        println("┌")
        println("│ m = $m, l = $l, r = $r")
        println("│ f(m) = ${fn.invokeWithoutCount(m)}, " +
                "f(l) = ${fn.invokeWithoutCount(l)}, " +
                "f(r) = ${fn.invokeWithoutCount(r)}")
        println("└")
    }
}
