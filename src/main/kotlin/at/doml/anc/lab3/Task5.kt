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
    val dx = properties.getProperty("dx").toDoubleArray()
    val precision = properties.getProperty("precision").toDouble()
    val t = properties.getProperty("t").toDouble()
    val fn = { x: DoubleArray -> (x[0] - 3).sq() + x[1].sq() }

    println("Function: (x₁ - 3)² + x₂²")
    println("Initial point: ${initialPoint.prettyString()}")
    println("Transforming problem and using Hooke-Jeeves algorithm...")
    println()

    var previous: Double
    val inequalityLimits = listOf(
            { x: DoubleArray -> 3.0 - x[0] - x[1] },
            { x: DoubleArray -> 3.0 + 1.5 * x[0] - x[1] }
    )

    var solution = InnerPointFinder(initialPoint, inequalityLimits, t, dx, precision)

    var dt = t
    var current = fn(solution)
    do {
        val transformedFunction = { p: DoubleArray ->
            LimitlessProblemTransformation(
                    p,
                    fn,
                    t = dt,
                    inequalityLimits = inequalityLimits,
                    equalityLimits = listOf(
                            { x -> x[1] - 1.0 }
                    )
            )
        }
        dt *= 10
        solution = HookeJeeves(solution, transformedFunction, dx, precision)
        previous = current
        current = fn(solution)
    } while (Math.abs(previous - current) > precision)

    println("Found solution: x = ${solution.prettyString()}, f(x) = ${fn(solution)}")
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
