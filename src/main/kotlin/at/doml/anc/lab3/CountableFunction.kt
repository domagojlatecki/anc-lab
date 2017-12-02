package at.doml.anc.lab3

class CountableFunction(private val fn: (DoubleArray) -> Double) {

    var count = 0

    operator fun invoke(input: DoubleArray): Double {
        count += 1
        return fn(input)
    }
}
