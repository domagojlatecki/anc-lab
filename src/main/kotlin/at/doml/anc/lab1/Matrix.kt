package at.doml.anc.lab1

import java.io.File
import java.lang.Math.abs

interface Matrix {

    val rows: Int
    val columns: Int

    fun copy(): Matrix

    fun newMatrix(rows: Int, columns: Int): Matrix

    operator fun get(row: Int, column: Int): Double

    operator fun get(row: Int): DoubleArray

    operator fun set(row: Int, column: Int, value: Double)

    private fun throwIncompatibleDimensionsException(other: Matrix): Nothing = throw IllegalArgumentException(
            "matrices have incompatible dimensions: [${this.rows}, ${this.columns}] vs" +
                    " [${other.rows}, ${other.columns}]"
    )

    private fun newMatrixWithElements(elementSupplier: (Int, Int) -> Double): Matrix {
        return this.newMatrixWithElements(this.rows, this.columns, elementSupplier)
    }

    private fun newMatrixWithElements(rows: Int, columns: Int, elementSupplier: (Int, Int) -> Double): Matrix {
        val new = this.newMatrix(rows, columns)

        for (i in 0 until rows) {
            for (j in 0 until columns) {
                new[i, j] = elementSupplier.invoke(i, j)
            }
        }

        return new
    }

    private fun binaryOperation(other: Matrix, binaryOperator: (Double, Double) -> Double): Matrix {
        if (this.rows != other.rows || this.columns != other.columns) {
            this.throwIncompatibleDimensionsException(other)
        }

        return this.newMatrixWithElements({ i, j -> binaryOperator(this[i, j], other[i, j]) })
    }

    operator fun plus(other: Matrix): Matrix = this.binaryOperation(other, Double::plus)

    operator fun minus(other: Matrix): Matrix = this.binaryOperation(other, Double::minus)

    operator fun times(other: Matrix): Matrix {
        if (this.columns != other.rows) {
            this.throwIncompatibleDimensionsException(other)
        }

        val new = this.newMatrix(this.rows, other.columns)

        for (i in 0 until this.rows) {
            for (j in 0 until other.columns) {
                new[i, j] = (0 until this.columns).sumByDouble { this[i, it] * other[it, j] }
            }
        }

        return new
    }

    operator fun times(scalar: Double): Matrix = this.newMatrixWithElements({ i, j -> this[i, j] * scalar })

    private fun checkElementEquals(other: Matrix): Boolean {
        for (i in 0 until this.rows) {
            for (j in 0 until this.columns) {
                if (abs(this[i, j] - other[i, j]) >= Matrix.delta) {
                    return false
                }
            }
        }

        return true
    }

    fun transpose(): Matrix = this.newMatrixWithElements(this.columns, this.rows, { i, j -> this[j, i] })

    fun inverse(tolerance: Double = 0.0): Matrix {
        if (this.columns != this.rows) {
            throw IllegalArgumentException("matrix must be square")
        }

        val (lu, transformations) = MatrixOperations.lupDecomposition(this, tolerance)
        return this.calculateInverse(lu, transformations)
    }

    private fun calculateInverse(lu: Matrix, transformations: MatrixTransformation): Matrix {
        val out = MutableMatrix(this.rows, this.columns)
        val dimension = this.rows

        for (i in 0 until dimension) {
            val vector = MutableMatrix(Array(dimension) { index ->
                if (index == i) doubleArrayOf(1.0) else doubleArrayOf(0.0)
            })
            val swapped = transformations.apply(vector)
            val y = MatrixOperations.forwardSubstitution(asLMatrix(lu), swapped)
            val x = MatrixOperations.backwardSubstitution(asUMatrix(lu), y)

            for (j in 0 until dimension) {
                out[j, i] = x[j, 0]
            }
        }

        return out
    }

    private fun asLMatrix(matrix: Matrix): Matrix {
        val new = matrix.copy()

        for (i in 0 until new.rows) {
            for (j in 0 until new.columns) {
                new[i, j] = when {
                    i == j -> 1.0
                    i < j -> 0.0
                    else -> matrix[i, j]
                }
            }
        }

        return new
    }

    private fun asUMatrix(matrix: Matrix): Matrix {
        val new = matrix.copy()

        for (i in 0 until new.rows) {
            for (j in 0 until new.columns) {
                new[i, j] = when {
                    i <= j -> matrix[i, j]
                    else -> 0.0
                }
            }
        }

        return new
    }

    fun equals(other: Matrix): Boolean = if (this.rows != other.rows || this.columns != other.columns) {
        false
    } else {
        this.checkElementEquals(other)
    }

    fun toPrettyString(): String

    fun toArrayString(): String

    override operator fun equals(other: Any?): Boolean

    companion object {

        private val delta = 10e-6

        fun writeToFile(file: File, matrix: Matrix) {
            file.writeText(matrix.toString())
        }
    }
}
