package at.doml.anc.lab3

object GradientDescent {

    private fun DoubleArray.norm(): Double {
        return Math.sqrt(this.map { x -> x * x }.sum())
    }

    private fun DoubleArray.sub(other: DoubleArray): DoubleArray {
        return DoubleArray(this.size) { i -> this[i] - other[i] }
    }

    private fun DoubleArray.mul(scalar: Double): DoubleArray {
        return DoubleArray(this.size) { i -> this[i] * scalar }
    }

    operator fun invoke(start: DoubleArray, function: CountableFunction,
                        gradient: CountableGradient, precision: Double = 10e-6,
                        useGoldenSectionSearch: Boolean = false): DoubleArray {
        var divergenceCounter = 0
        var step = gradient(start)
        var best = start
        var bestFnValue = function(best)
        var current = start

        while (step.norm() > precision) {
            if (divergenceCounter >= 100) {
                println("Divergence detected, stopping algorithm.")
                println()

                return best
            }

            val next = if (useGoldenSectionSearch) {
                val optStepFn = { l: Double -> function(current.sub(step.mul(l))) }
                val optStepL = GoldenSectionSearch(0.0, 10.0, optStepFn, precision)
                current.sub(step.mul(optStepL))
            } else {
                current.sub(step)
            }

            val nextFnValue = function(next)

            if (nextFnValue < bestFnValue) {
                best = next
                bestFnValue = nextFnValue
                divergenceCounter = 0
            } else {
                divergenceCounter += 1
            }

            step = gradient(next)
            current = next
        }

        return best
    }
}
