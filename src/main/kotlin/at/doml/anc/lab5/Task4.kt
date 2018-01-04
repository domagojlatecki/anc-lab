package at.doml.anc.lab5

import at.doml.anc.lab1.Matrix
import at.doml.anc.lab1.MutableMatrix
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.util.Properties
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    if (args.size != 4) {
        showUsage()
    }

    val properties = Properties()

    properties.load(Files.newInputStream(Paths.get(args[3])))

    val step = properties.getProperty("step").toDouble()
    val tMax = properties.getProperty("tMax").toDouble()
    val verbose = properties.getProperty("verbose").toBoolean()
    val printStep = properties.getProperty("printStep").toInt()
    val skipRungeKutt = properties.getProperty("skipRungeKutt").toBoolean()
    val skipTrapezoid = properties.getProperty("skipTrapezoid").toBoolean()
    val matrixA = load(args[0])
    val matrixB = load(args[1])
    val x0 = load(args[2])

    println("Input equation:")
    println("ẋ = Ax⃗ + B")
    println()
    println("A =")
    println(matrixA.toPrettyString())
    println("B =")
    println(matrixB.toPrettyString())
    println("x⃗₀ =")
    println(x0.toPrettyString())
    println()
    println("Step: $step")
    println("Interval: [0.0, $tMax]")
    println()
    println()

    if (!skipRungeKutt) {
        println("Solving system using Runge-Kutt method...")

        var t = 0.0
        var i = 0
        var x = x0

        while (t <= tMax) {
            val m1 = matrixA * x + matrixB
            val m2 = matrixA * (x + m1 * (step / 2.0)) + matrixB
            val m3 = matrixA * (x + m2 * (step / 2.0)) + matrixB
            val m4 = matrixA * (x + m3 * step) + matrixB

            x += (m1 + m2 + m2 + m3 + m3 + m4) * (step / 6.0)
            i += 1
            t += step

            if (verbose && i % printStep == 0) {
                println("t=$t")
                println("x=${x.toArrayString()}")
                println()
            }
        }

        println()
        println("Solution for t = $tMax:")
        println(x.toPrettyString())
        println()
        println()
    }

    if (!skipTrapezoid) {
        println("Solving system using trapezoid method...")

        var t = 0.0
        var i = 0
        var x = x0
        val identityMatrix = MutableMatrix(matrixA.rows, matrixA.columns)

        for (j in 0 until identityMatrix.rows) {
            identityMatrix[j, j] = 1.0
        }

        while (t <= tMax) {
            val aTimesHalfStep = matrixA * (step / 2.0)
            val inverse = (identityMatrix - aTimesHalfStep).inverse()
            val r = inverse * (identityMatrix + aTimesHalfStep)
            val s = (inverse * step) * matrixB

            x = r * x + s
            i += 1
            t += step

            if (verbose && i % printStep == 0) {
                println("t=$t")
                println("x=${x.toArrayString()}")
                println()
            }
        }

        println()
        println("Solution for t = $tMax:")
        println(x.toPrettyString())
        println()
        println()
    }
}

private fun showUsage(): Nothing {
    println("""Usage:
                  | matrixAFile matrixBFile xt0File propertiesFile""".trimMargin())
    exitProcess(1)
}

private fun load(path: String): Matrix {
    try {
        return MutableMatrix.fromFile(File(path))
    } catch (e: Exception) {
        println("Unable to read provided file: $path")
        exitProcess(1)
    }
}
