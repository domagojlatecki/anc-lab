package at.doml.anc.lab2

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
    val alpha = properties.getProperty("alpha").toDouble()
    val beta = properties.getProperty("beta").toDouble()
    val gamma = properties.getProperty("gamma").toDouble()
    val sigma = properties.getProperty("sigma").toDouble()

    fun findSolution(
            initialPoint: DoubleArray,
            function: CountableFunction<DoubleArray>,
            functionStr: String
    ) {
        println("Function: $functionStr")
        println("Finding solution using Nelder-Mead simplex search...")
        println()

        val solution = MultiSearchers.nelderMeadSimplexSearch(
                initialPoint = initialPoint,
                fn = function,
                precision = precision,
                verbose = verbose,
                alpha = alpha,
                beta = beta,
                gamma = gamma,
                sigma = sigma
        )
        println("Found solution: x = ${solution.prettyString()}, f(x) = ${function(solution)}")
        println("Total number of function evaluations: ${function.count}")
        println()
    }

    findSolution(
            doubleArrayOf(0.5, 0.5),
            CountableFunction { x -> 100.0 * (x[1] - x[0].sq()).sq() + (1.0 - x[0]).sq() },
            "100 · (x₂ - x₁²)² + (1 - x₁)²"
    )
    findSolution(
            doubleArrayOf(20.0, 20.0),
            CountableFunction { x -> 100.0 * (x[1] - x[0].sq()).sq() + (1.0 - x[0]).sq() },
            "100 · (x₂ - x₁²)² + (1 - x₁)²"
    )
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
