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
    val dx1 = properties.getProperty("dx1").toDoubleArray()
    val dx2 = properties.getProperty("dx2").toDoubleArray()
    val precision = properties.getProperty("precision").toDouble()
    val t = properties.getProperty("t").toDouble()

    fun findSolution(functionString: String, initialPoint: DoubleArray, dx: DoubleArray, fn: (DoubleArray) -> Double) {
        println("Function: $functionString")
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
        var current = fn(initialPoint)
        do {
            val transformedFunction = { p: DoubleArray ->
                LimitlessProblemTransformation(
                        p,
                        fn,
                        t,
                        listOf(
                                { x -> x[1] - x[0] },
                                { x -> 2.0 - x[0] }
                        ),
                        emptyList()
                )
            }
            dt *= 10
            solution = HookeJeeves(solution, transformedFunction, dx, precision)
            previous = current
            current = fn(solution)
        } while (Math.abs(previous - current) > precision)

        println("Found solution: x = ${solution.prettyString()}, f(x) = ${fn(solution)}")
    }

    findSolution(
            "100 · (x₂ - x₁²)² + (1 - x₁)²",
            initialPoint1,
            dx1,
            { x -> 100.0 * (x[1] - x[0].sq()).sq() + (1.0 - x[0]).sq() }
    )
    println()
    findSolution(
            "(x₁ - 4)² + 4 · (x₂ - 2)²",
            initialPoint2,
            dx2,
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
