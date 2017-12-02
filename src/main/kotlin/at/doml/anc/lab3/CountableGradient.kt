package at.doml.anc.lab3

class CountableGradient(private val fn: (DoubleArray) -> DoubleArray) {

    var count = 0

    operator fun invoke(input: DoubleArray): DoubleArray {
        count += 1
        return fn(input)
    }

    fun resetCount() {
        this.count = 0
    }
}
