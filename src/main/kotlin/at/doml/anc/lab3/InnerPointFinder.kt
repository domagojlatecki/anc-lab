package at.doml.anc.lab3

object InnerPointFinder {

    operator fun invoke(point: DoubleArray, inequalityLimits: List<(DoubleArray) -> Double>, t: Double,
                        dx: DoubleArray, precision: Double): DoubleArray {
        return if (inequalityLimits.all { limit -> limit(point) >= 0.0 }) {
            point
        } else {
            HookeJeeves(
                    point,
                    { x ->
                        inequalityLimits.map { limit ->
                            val value = limit(x)

                            if (value >= 0.0) {
                                0.0
                            } else {
                                t * value
                            }
                        }.sum() * -1.0
                    },
                    dx,
                    precision
            )
        }
    }
}
