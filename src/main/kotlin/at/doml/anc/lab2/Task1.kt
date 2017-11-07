package at.doml.anc.lab2

import java.nio.file.Files
import java.nio.file.Paths
import java.util.Properties
import kotlin.system.exitProcess

private val function: CountableFunction<Double> = CountableFunction { x -> (x - 3) * (x - 3) }
private val functionArr: CountableFunction<DoubleArray> = CountableFunction { x -> (x[0] - 3) * (x[0] - 3) }

fun main(args: Array<String>) {
    if (args.size != 1) {
        println("Path to property file is expected.")
        exitProcess(1)
    }

    val properties = Properties()

    properties.load(Files.newInputStream(Paths.get(args[0])))

    val initialPoint = properties.getProperty("initialPoint").toDouble()
    val precision = properties.getProperty("precision").toDouble()
    val verbose = properties.getProperty("verbose").toBoolean()
    val h = properties.getProperty("h").toDouble()
    val epsilon = doubleArrayOf(properties.getProperty("epsilon").toDouble())
    val dx = doubleArrayOf(properties.getProperty("dx").toDouble())
    val alpha = properties.getProperty("alpha").toDouble()
    val beta = properties.getProperty("beta").toDouble()
    val gamma = properties.getProperty("gamma").toDouble()
    val sigma = properties.getProperty("sigma").toDouble()

    println("Finding unimodal interval for point: $initialPoint")
    println()

    val (start, end) = Algorithms.unimodalInterval(
            point = initialPoint,
            fn = function,
            verbose = verbose,
            h = h
    )

    if (verbose) {
        println()
    }

    println("Interval: [$start, $end]")
    println("Total number of function evaluations: ${function.count}")
    println()
    println("Finding solution using golden-section search...")
    println()

    function.resetCount()

    val solution = Algorithms.goldenSectionSearch(
            start = start,
            end = end,
            fn = function,
            precision = precision,
            verbose = verbose
    )
    println("Found solution: x = $solution, f(x) = ${function(solution)}")
    println("Total number of function evaluations: ${function.count}")
    println()
    println("Finding solution using coordinate search...")
    println()

    val solution2 = MultiSearchers.coordinateSearch(
            initialPoint = doubleArrayOf(initialPoint),
            fn = functionArr,
            precision = precision,
            verbose = verbose,
            h = h,
            epsilon = epsilon
    )
    println("Found solution: x = ${solution2[0]}, f(x) = ${functionArr(solution2)}")
    println("Total number of function evaluations: ${functionArr.count}")
    println()
    println("Finding solution using Hooke-Jeeves search...")
    println()

    functionArr.resetCount()

    val solution3 = MultiSearchers.hookeJeevesSearch(
            initialPoint = doubleArrayOf(initialPoint),
            fn = functionArr,
            precision = precision,
            verbose = verbose,
            dx = dx
    )
    println("Found solution: x = ${solution3[0]}, f(x) = ${functionArr(solution3)}")
    println("Total number of function evaluations: ${functionArr.count}")
    println()
    println("Finding solution using Nelder-Mead simplex search...")
    println()

    functionArr.resetCount()

    val solution4 = MultiSearchers.nelderMeadSimplexSearch(
            initialPoint = doubleArrayOf(initialPoint),
            fn = functionArr,
            precision = precision,
            verbose = verbose,
            alpha = alpha,
            beta = beta,
            gamma = gamma,
            sigma = sigma
    )
    println("Found solution: x = ${solution4[0]}, f(x) = ${functionArr(solution4)}")
    println("Total number of function evaluations: ${functionArr.count}")
}
