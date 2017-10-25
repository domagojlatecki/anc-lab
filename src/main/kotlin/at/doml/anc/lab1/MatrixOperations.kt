package at.doml.anc.lab1

import java.lang.Math.abs

object MatrixOperations {

    private val tolerance = 10e-6

    private fun checkIfDimensionsAreCompatible(matrix: Matrix, vector: Matrix) {
        this.checkIfSquare(matrix)

        if (vector.columns != 1) {
            throw IllegalArgumentException("invalid vector dimension: [${vector.rows}, ${vector.columns}]")
        }

        if (matrix.rows != vector.rows) {
            throw IllegalArgumentException("incompatible matrix and vector dimensions:" +
                    " [${matrix.rows}, ${matrix.columns}] vs [${vector.rows}, ${vector.columns}]")
        }
    }

    private fun safeDiv(a: Double, b: Double): Double {
        if (abs(b) <= this.tolerance) {
            throw ArithmeticException("division by zero or near-zero number: $a / $b")
        }

        return a / b
    }

    fun forwardSubstitution(matrix: Matrix, vector: Matrix): Matrix {
        this.checkIfDimensionsAreCompatible(matrix, vector)

        val n = matrix.rows
        val out = MutableMatrix(n, 1)

        for (i in 0 until n) {
            out[i, 0] = vector[i, 0]

            for (j in 0 until i) {
                out[i, 0] -= matrix[i, j] * out[j, 0]
            }

            out[i, 0] = this.safeDiv(out[i, 0], matrix[i, i])
        }

        return out
    }

    fun backwardSubstitution(matrix: Matrix, vector: Matrix): Matrix {
        this.checkIfDimensionsAreCompatible(matrix, vector)

        val n = matrix.rows
        val out = MutableMatrix(n, 1)

        for (i in n - 1 downTo 0) {
            out[i, 0] = vector[i, 0]

            for (j in i + 1 until n) {
                out[i, 0] -= matrix[i, j] * out[j, 0]
            }

            out[i, 0] = this.safeDiv(out[i, 0], matrix[i, i])
        }

        return out
    }

    private fun checkIfSquare(matrix: Matrix) {
        if (matrix.rows != matrix.columns) {
            throw IllegalArgumentException("provided matrix is not square: [${matrix.rows}, ${matrix.columns}]")
        }
    }

    fun luDecomposition(matrix: Matrix): Matrix {
        this.checkIfSquare(matrix)

        val n = matrix.rows
        val lu = matrix.copy()

        for (i in 0 until n) {
            for (j in i + 1 until n) {
                lu[j, i] = this.safeDiv(lu[j, i], lu[i, i])

                for (k in i + 1 until n) {
                    lu[j, k] = lu[j, k] - lu[j, i] * lu[i, k]
                }
            }
        }

        return lu
    }

    fun lupDecomposition(matrix: Matrix, tolerance: Double = 0.0): Pair<Matrix, MatrixTransformation> {
        this.checkIfSquare(matrix)

        val n = matrix.rows
        val lu = matrix.copy()
        val transformation = MatrixTransformation(n, n)

        for (i in 0 until n) {
            var pivot = 0.0
            var iMax = i

            for (j in i until n) {
                val v = Math.abs(lu[j, i])

                if (v > pivot) {
                    pivot = v
                    iMax = j
                }
            }

            if (Math.abs(pivot) <= tolerance) {
                throw IllegalStateException("pivot value is lower than specified tolerance: $pivot <= $tolerance")
            }

            if (iMax != i) {
                transformation.swapRows(i, iMax)

                for (j in 0 until n) {
                    val tmp = lu[i, j]

                    lu[i, j] = lu[iMax, j]
                    lu[iMax, j] = tmp
                }
            }

            for (j in i + 1 until n) {
                lu[j, i] = this.safeDiv(lu[j, i], lu[i, i])

                for (k in i + 1 until n) {
                    lu[j, k] = lu[j, k] - lu[j, i] * lu[i, k]
                }
            }
        }

        return Pair(lu, transformation)
    }
}
