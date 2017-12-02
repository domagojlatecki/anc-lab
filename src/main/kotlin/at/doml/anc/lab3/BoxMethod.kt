package at.doml.anc.lab3

object BoxMethod {

    private fun DoubleArray.add(other: DoubleArray): DoubleArray {
        return DoubleArray(this.size) { i -> this[i] + other[i] }
    }

    private fun DoubleArray.sub(other: DoubleArray): DoubleArray {
        return DoubleArray(this.size) { i -> this[i] - other[i] }
    }

    private fun DoubleArray.mul(scalar: Double): DoubleArray {
        return DoubleArray(this.size) { i -> this[i] * scalar }
    }

    private fun List<DoubleArray>.centroid(): DoubleArray {
        return this.reduce { l, r -> l.add(r) }.map { v -> v / this.size }.toDoubleArray()
    }

    private fun DoubleArray.allGreaterOrEqualTo(other: DoubleArray): Boolean {
        return (0 until this.size).all { this[it] >= other[it] }
    }

    operator fun invoke(start: DoubleArray, function: CountableFunction, min: DoubleArray, max: DoubleArray,
                        implicitConstraints: (DoubleArray) -> Boolean, alpha: Double = 1.3,
                        precision: Double = 10e-6): DoubleArray {
        return if (start.allGreaterOrEqualTo(min) && max.allGreaterOrEqualTo(start) && implicitConstraints(start)) {
            boxAlgorithm(start, function, min, max, implicitConstraints, alpha, precision)
        } else {
            println("Initial point doesn't satisfy given constraints.")
            println()

            start
        }
    }

    private fun DoubleArray.argMax(): Int {
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

    private fun DoubleArray.argMin(): Int {
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

    private fun DoubleArray.secondWorst(): Int {
        var max = this[0]
        var arg = 0
        var oldArg = 0

        for (i in this.indices) {
            if (this[i] >= max) {
                max = this[i]
                oldArg = arg
                arg = i
            }
        }

        return oldArg
    }

    private fun Double.sq(): Double = this * this

    private fun boxAlgorithm(start: DoubleArray, function: CountableFunction, min: DoubleArray, max: DoubleArray,
                             implicitConstraints: (DoubleArray) -> Boolean, alpha: Double,
                             precision: Double): DoubleArray {
        var centroid = start
        val acceptedPoints = mutableListOf(start)

        for (i in 0 until 2 * start.size) {
            val newPoint = DoubleArray(start.size) { _ -> 0.0 }

            for (j in 0 until start.size) {
                newPoint[j] = min[j] + Math.random() * (max[j] - min[j])
            }

            while (!implicitConstraints(newPoint)) {
                for (j in 0 until start.size) {
                    newPoint[j] = 0.5 * (newPoint[j] + centroid[j])
                }
            }

            acceptedPoints.add(newPoint)
            centroid = acceptedPoints.centroid()
        }

        var best = start
        var bestFnValue = function(start)
        var divergenceCounter = 0

        do {
            if (divergenceCounter >= 100) {
                return best
            }

            val fnValues = acceptedPoints.map { x -> function(x) }.toDoubleArray()
            val worstIndex = fnValues.argMax()
            val worst = acceptedPoints[worstIndex]
            val secondWorstIndex = fnValues.secondWorst()
            val acceptedPointsWithoutWorst = acceptedPoints.filterIndexed { index, _ -> index != worstIndex }
            val center = acceptedPointsWithoutWorst.centroid()
            val reflection = center.mul(1.0 + alpha).sub(worst.mul(alpha))

            for (i in 0 until start.size) {
                if (reflection[i] < min[i]) {
                    reflection[i] = min[i]
                } else if (reflection[i] > max[i]) {
                    reflection[i] = max[i]
                }
            }

            while (!implicitConstraints(reflection)) {
                for (j in 0 until start.size) {
                    reflection[j] = 0.5 * (reflection[j] + center[j])
                }
            }

            if (function(reflection) > fnValues[secondWorstIndex]) {
                for (j in 0 until start.size) {
                    reflection[j] = 0.5 * (reflection[j] + center[j])
                }
            }

            acceptedPoints[worstIndex] = reflection
            fnValues[worstIndex] = function(reflection)

            val bestIndex = fnValues.argMin()
            if (fnValues[bestIndex] < bestFnValue) {
                best = acceptedPoints[fnValues.argMin()]
                bestFnValue = fnValues[bestIndex]
                divergenceCounter = 0
            } else {
                divergenceCounter += 1
            }

            val centerValue = function(center)
            val sum = (0 until acceptedPoints.size).map { i -> (fnValues[i] - centerValue).sq() }.sum()
        } while (Math.sqrt(sum / acceptedPoints.size) > precision)

        return best
    }
}
