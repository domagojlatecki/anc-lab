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
        println("Finding solution using Hooke-Jeeves search...")
        println()

        function.resetCount()

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
        println("Finding solution using Nelder-Mead simplex search...")
        println()

        function.resetCount()

        val solution2 = MultiSearchers.nelderMeadSimplexSearch(
                initialPoint = initialPoint,
                fn = function,
                precision = precision,
                verbose = verbose,
                alpha = alpha,
                beta = beta,
                gamma = gamma,
                sigma = sigma
        )
        println("Found solution: x = ${solution2.prettyString()}, f(x) = ${function(solution2)}")
        println("Total number of function evaluations: ${function.count}")
        println()
    }

    findSolution(
            doubleArrayOf(5.0, 5.0),
            CountableFunction { x -> Math.abs((x[0] - x[1]) * (x[0] + x[1])) + Math.sqrt(x[0].sq() + x[1].sq()) },
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
