package at.doml.anc.lab3

object LimitlessProblemTransformation {

    private fun Double.sq(): Double = this * this

    operator fun invoke(point: DoubleArray, function: (DoubleArray) -> Double, t: Double,
                        inequalityLimits: List<(DoubleArray) -> Double>,
                        equalityLimits: List<(DoubleArray) -> Double>): Double {
        val logSums = inequalityLimits.map { limit ->
            val value = limit(point)

            if (value >= 0.0) {
                Math.log(value)
            } else {
                -Double.POSITIVE_INFINITY
            }
        }.sum()

        val squareSums = equalityLimits.map { limit -> limit(point).sq() }.sum()

        return function(point) - (1.0 / t) * logSums + t * squareSums
    }
}
