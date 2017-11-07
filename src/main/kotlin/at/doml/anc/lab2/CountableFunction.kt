package at.doml.anc.lab2

class CountableFunction<in T>(private val fn: (T) -> Double) {

    var count = 0

    operator fun invoke(input: T): Double {
        count += 1
        return fn(input)
    }

    fun invokeWithoutCount(input: T): Double = fn(input)

    fun resetCount() {
        this.count = 0
    }
}
