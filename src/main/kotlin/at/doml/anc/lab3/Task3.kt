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

    val initialPoint1 = properties.getProperty("initialPoint1").toDoubleArray()
    val initialPoint2 = properties.getProperty("initialPoint2").toDoubleArray()
    val min = properties.getProperty("min").toDoubleArray()
    val max = properties.getProperty("max").toDoubleArray()
    val alpha = properties.getProperty("alpha").toDouble()
    val precision = properties.getProperty("precision").toDouble()

    fun findSolution(functionString: String, initialPoint: DoubleArray, fn: (DoubleArray) -> Double) {
        println("Function: $functionString")
        println("Initial point: ${initialPoint.prettyString()}")
        println("Using Box method...")
        println()

        val function = CountableFunction(fn)
        val solution = BoxMethod(
                initialPoint,
                function,
                min,
                max,
                { x -> x[1] - x[0] >= 0.0 && 2.0 - x[0] >= 0.0 },
                alpha,
                precision
        )

        println("Found solution: x = ${solution.prettyString()}, f(x) = ${function(solution)}")
        println("Number of function invocations: ${function.count}")
    }

    findSolution(
            "100 · (x₂ - x₁²)² + (1 - x₁)²",
            initialPoint1,
            { x -> 100.0 * (x[1] - x[0].sq()).sq() + (1.0 - x[0]).sq() }
    )
    println()
    findSolution(
            "(x₁ - 4)² + 4 · (x₂ - 2)²",
            initialPoint2,
            { x -> (x[0] - 4).sq() + 4 * (x[1] - 2).sq() }
    )
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
