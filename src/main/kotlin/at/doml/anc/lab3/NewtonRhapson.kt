package at.doml.anc.lab3

import at.doml.anc.lab1.Matrix
import at.doml.anc.lab1.MatrixOperations
import at.doml.anc.lab1.MutableMatrix

object NewtonRhapson {

    private fun DoubleArray.norm(): Double {
        return Math.sqrt(this.map { x -> x * x }.sum())
    }

    private fun DoubleArray.sub(other: DoubleArray): DoubleArray {
        return DoubleArray(this.size) { i -> this[i] - other[i] }
    }

    private fun DoubleArray.mul(scalar: Double): DoubleArray {
        return DoubleArray(this.size) { i -> this[i] * scalar }
    }

    private fun Matrix.invert(): Matrix {
        val dimension = this.rows
        val (lu, transformations) = MatrixOperations.lupDecomposition(this)
        val out = MutableMatrix(dimension, dimension)

        for (i in 0 until dimension) {
            val vector = coordinateUnitVector(dimension, i)
            val transformedVector = transformations.apply(vector)
            val y = MatrixOperations.forwardSubstitution(lu.asLMatrix(), transformedVector)
            val x = MatrixOperations.backwardSubstitution(lu.asUMatrix(), y)
            out[i] = x
        }

        return out
    }

    private fun coordinateUnitVector(size: Int, index: Int): Matrix {
        val vector = MutableMatrix(size, 1)
        vector[index, 0] = 1.0
        return vector
    }

    private fun Matrix.asLMatrix(): Matrix {
        val new = this.copy()

        for (i in 0 until new.rows) {
            for (j in 0 until new.columns) {
                new[i, j] = when {
                    i == j -> 1.0
                    i < j -> 0.0
                    else -> this[i, j]
                }
            }
        }

        return new
    }

    private fun Matrix.asUMatrix(): Matrix {
        val new = this.copy()

        for (i in 0 until new.rows) {
            for (j in 0 until new.columns) {
                new[i, j] = when {
                    i <= j -> this[i, j]
                    else -> 0.0
                }
            }
        }

        return new
    }

    private operator fun Matrix.set(column: Int, values: Matrix) {
        for (row in 0 until this.rows) {
            this[row, column] = values[row, 0]
        }
    }

    private fun calculateStep(point: DoubleArray, gradient: CountableGradient,
                              hesseMatrix: CountableHesseMatrix): DoubleArray {
        val invertedHesse = hesseMatrix(point).invert()
        val gradientMatrix = MutableMatrix(Array(1) { _ -> gradient(point) }).transpose()
        val invertedHesseTimesGradient = (invertedHesse * gradientMatrix).transpose()
        return invertedHesseTimesGradient[0]
    }

    operator fun invoke(start: DoubleArray, function: CountableFunction,
                        gradient: CountableGradient, hesseMatrix: CountableHesseMatrix,
                        precision: Double = 10e-6, useGoldenSectionSearch: Boolean = false): DoubleArray {
        var divergenceCounter = 0
        var step = calculateStep(start, gradient, hesseMatrix)
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

            step = calculateStep(next, gradient, hesseMatrix)
            current = next
        }

        return best
    }
}
