package at.doml.anc.lab3

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

    val initialPoint = properties.getProperty("initialPoint").toDoubleArray()
    val precision = properties.getProperty("precision").toDouble()
    val useGoldenSectionSearch = properties.getProperty("useGoldenSectionSearch").toBoolean()

    println("Function: (x₁ - 4)² + 4 · (x₂ - 2)²")
    println("Initial point: ${initialPoint.prettyString()}")
    println("Using gradient descent${if (useGoldenSectionSearch) " with GSS" else ""}...")
    println()

    val function = CountableFunction { x -> (x[0] - 4).sq() + 4 * (x[1] - 2).sq() }
    val gradient = CountableGradient { x ->
        DoubleArray(2) { i ->
            if (i == 0) {
                2 * (x[0] - 4)
            } else {
                8 * (x[1] - 2)
            }
        }
    }
    val solution = GradientDescent(initialPoint, function, gradient, precision, useGoldenSectionSearch)

    println("Found solution: x = ${solution.prettyString()}, f(x) = ${function(solution)}")
    println("Number of function invocations: ${function.count}")
    println("Number of gradient invocations: ${gradient.count}")
}

private fun Double.sq(): Double = this * this

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
