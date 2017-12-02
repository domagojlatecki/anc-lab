package at.doml.anc.lab3

import at.doml.anc.lab1.Matrix

class CountableHesseMatrix(private val fn: (DoubleArray) -> Matrix) {

    var count = 0

    operator fun invoke(input: DoubleArray): Matrix {
        count += 1
        return fn(input)
    }

    fun resetCount() {
        this.count = 0
    }
}
