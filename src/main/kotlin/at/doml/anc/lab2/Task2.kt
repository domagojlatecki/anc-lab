package at.doml.anc.lab2

import java.lang.Math.abs
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
    val h = properties.getProperty("h").toDouble()
    val epsilon = properties.getProperty("epsilon").toDoubleArray()
    val dx = properties.getProperty("dx").toDoubleArray()
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
        println("Finding solution using coordinate search...")
        println()

        val solution = MultiSearchers.coordinateSearch(
                initialPoint = initialPoint,
                fn = function,
                precision = precision,
                verbose = verbose,
                h = h,
                epsilon = epsilon
        )
        println("Found solution: x = ${solution.prettyString()}, f(x) = ${function(solution)}")
        println("Total number of function evaluations: ${function.count}")
        println()
        println("Finding solution using Hooke-Jeeves search...")
        println()

        function.resetCount()

        val solution2 = MultiSearchers.hookeJeevesSearch(
                initialPoint = initialPoint,
                fn = function,
                precision = precision,
                verbose = verbose,
                dx = dx
        )
        println("Found solution: x = ${solution2.prettyString()}, f(x) = ${function(solution2)}")
        println("Total number of function evaluations: ${function.count}")
        println()
        println("Finding solution using Nelder-Mead simplex search...")
        println()

        function.resetCount()

        val solution3 = MultiSearchers.nelderMeadSimplexSearch(
                initialPoint = initialPoint,
                fn = function,
                precision = precision,
                verbose = verbose,
                alpha = alpha,
                beta = beta,
                gamma = gamma,
                sigma = sigma
        )
        println("Found solution: x = ${solution3.prettyString()}, f(x) = ${function(solution3)}")
        println("Total number of function evaluations: ${function.count}")
        println()
    }

    findSolution(
            doubleArrayOf(-1.9, 2.0),
            CountableFunction { x -> 100.0 * (x[1] - x[0].sq()).sq() + (1.0 - x[0]).sq() },
            "100 · (x₂ - x₁²)² + (1 - x₁)²"
    )
    findSolution(
            doubleArrayOf(0.1, 0.3),
            CountableFunction { x -> (x[0] - 4).sq() + 4 * (x[1] - 2).sq() },
            "(x₁ - 4)² + 4 · (x₂ - 2)²"
    )
    findSolution(
            doubleArrayOf(0.0, 0.0, 0.0, 0.0, 0.0),
            CountableFunction { x ->
                (x[0] - 1).sq() + (x[1] - 2).sq() + (x[2] - 3).sq() + (x[3] - 4).sq() + (x[4] - 5).sq()
            },
            "∑ₖ (xₖ - k)²"
    )
    findSolution(
            doubleArrayOf(5.1, 1.1),
            CountableFunction { x -> abs((x[0] - x[1]) * (x[0] + x[1])) + sqrt(x[0].sq() + x[1].sq()) },
            "|(x₁ - x₂) · (x₁ + x₂)| + √(x₁² + x₂²)"
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
