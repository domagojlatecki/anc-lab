package at.doml.anc.lab2

import java.lang.Math.random
import java.lang.Math.sin
import java.lang.Math.sqrt
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

    val precision = properties.getProperty("precision").toDouble()
    val verbose = properties.getProperty("verbose").toBoolean()
    val dx = properties.getProperty("dx").toDoubleArray()

    fun findSolution(
            initialPoint: DoubleArray,
            function: CountableFunction<DoubleArray>,
            functionStr: String
    ) {
        println("Function: $functionStr")
        println("Initial point: ${initialPoint.prettyString()}")
        println("Finding solution using Hooke-Jeeves search...")
        println()

        val solution = MultiSearchers.hookeJeevesSearch(
                initialPoint = initialPoint,
                fn = function,
                precision = precision,
                verbose = verbose,
                dx = dx
        )
        println("Found solution: x = ${solution.prettyString()}, f(x) = ${function(solution)}")
        println("Total number of function evaluations: ${function.count}")
        println()
    }

    for (i in 0..10) {
        val point = doubleArrayOf(random() * 50.0, random() * 50.0)

        findSolution(
                point,
                CountableFunction { x ->
                    0.5 + (sin(sqrt(x.map { it.sq() }.sum())).sq() - 0.5) /
                            (1 + 0.001 * x.map { it.sq() }.sum()).sq()
                },
                "0.5 + (sin²√(∑ₖxₖ²) - 0.5) / (1 + 0.001 · ∑ₖxₖ²)²"
        )
    }
}

private fun Double.sq() = this * this
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
