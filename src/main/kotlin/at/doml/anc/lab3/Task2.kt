package at.doml.anc.lab3

import at.doml.anc.lab1.Matrix
import at.doml.anc.lab1.MutableMatrix
import java.nio.file.Files
import java.nio.file.Paths
import java.util.Properties
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    if (args.size != 1) {
        println("Path to property file is expected.")
        exitProcess(1)
    }

    val properties = Properties()

    properties.load(Files.newInputStream(Paths.get(args[0])))

    val initialPoint1 = properties.getProperty("initialPoint1").toDoubleArray()
    val initialPoint2 = properties.getProperty("initialPoint2").toDoubleArray()
    val precision = properties.getProperty("precision").toDouble()
    val useGoldenSectionSearch = properties.getProperty("useGoldenSectionSearch").toBoolean()

    fun findSolution(functionString: String, initialPoint: DoubleArray, fn: (DoubleArray) -> Double,
                     grad: (DoubleArray) -> DoubleArray, hesse: (DoubleArray) -> Matrix) {
        println("Function: $functionString")
        println("Initial point: ${initialPoint.prettyString()}")
        println("Using gradient descent${if (useGoldenSectionSearch) " with GSS" else ""}...")
        println()

        val function1 = CountableFunction(fn)
        val gradient1 = CountableGradient(grad)
        val solution1 = GradientDescent(initialPoint, function1, gradient1, precision, useGoldenSectionSearch)

        println("Found solution: x = ${solution1.prettyString()}, f(x) = ${function1(solution1)}")
        println("Number of function invocations: ${function1.count}")
        println("Number of gradient invocations: ${gradient1.count}")
        println()
        println("Function: $functionString")
        println("Initial point: ${initialPoint.prettyString()}")
        println("Using Newton-Rhapson method${if (useGoldenSectionSearch) " with GSS" else ""}...")
        println()

        val function2 = CountableFunction(fn)
        val gradient2 = CountableGradient(grad)
        val hesseMatrix = CountableHesseMatrix(hesse)
        val solution2 = NewtonRhapson(
                initialPoint, function2, gradient2,
                hesseMatrix, precision, useGoldenSectionSearch
        )

        println("Found solution: x = ${solution2.prettyString()}, f(x) = ${function2(solution2)}")
        println("Number of function invocations: ${function2.count}")
        println("Number of gradient invocations: ${gradient2.count}")
        println("Number of Hesse matrix invocations: ${hesseMatrix.count}")
    }

    findSolution(
            "100 · (x₂ - x₁²)² + (1 - x₁)²",
            initialPoint1,
            { x -> 100.0 * (x[1] - x[0].sq()).sq() + (1.0 - x[0]).sq() },
            { x ->
                DoubleArray(2) { i ->
                    if (i == 0) {
                        2.0 * (200.0 * x[0].qb() - 200.0 * x[0] * x[1] + x[0] - 1.0)
                    } else {
                        200.0 * (x[1] - x[0].sq())
                    }
                }
            },
            { x ->
                MutableMatrix(arrayOf(
                        doubleArrayOf(
                                1200.0 * x[0].sq() - 400.0 * x[1] + 2.0,
                                -400.0 * x[0]
                        ),
                        doubleArrayOf(
                                -400.0 * x[0],
                                200.0
                        )
                ))
            }
    )
    println()
    findSolution(
            "(x₁ - 4)² + 4 · (x₂ - 2)²",
            initialPoint2,
            { x -> (x[0] - 4).sq() + 4 * (x[1] - 2).sq() },
            { x ->
                DoubleArray(2) { i ->
                    if (i == 0) {
                        2 * (x[0] - 4)
                    } else {
                        8 * (x[1] - 2)
                    }
                }
            },
            { x ->
                MutableMatrix(arrayOf(
                        doubleArrayOf(
                                2.0,
                                0.0
                        ),
                        doubleArrayOf(
                                0.0,
                                8.0
                        )
                ))
            }
    )
}


private fun Double.sq(): Double = this * this

private fun Double.qb(): Double = this * this * this

private fun String.toDoubleArray(): DoubleArray {
    return this.trim().substring(1, this.length - 1)
            .split(',')
            .map { it.toDouble() }
            .toDoubleArray()
}

private fun DoubleArray.prettyString(): String {
    return this.joinToString(
            prefix = "{",
            separator = ", ",
            postfix = "}"
    )
}
