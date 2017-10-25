package at.doml.anc.lab1

import java.io.File
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    if (args.size < 3 || args.size > 4) {
        showUsage()
    }

    when {
        args[0] == "-lu" -> safeInvoke { luSolve(args) }
        args[0] == "-lup" -> safeInvoke { lupSolve(args) }
        else -> showUsage()
    }
}

private fun safeInvoke(function: () -> Unit) {
    try {
        function.invoke()
    } catch (e: Exception) {
        println("${e.message?.capitalize()}, aborting.")
        exitProcess(1)
    }
}

private fun showUsage(): Nothing {
    println("""Usage:
                  |-lu  matrixFile  vectorFile
                  |-lup [tolerance] matrixFile vectorFile""".trimMargin())
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

private fun printMatrix(prefix: String, matrix: Matrix) {
    val matrixRows = matrix.toPrettyString().split("\n")
    val padding = "".padStart(prefix.length + 3)
    val middle = matrixRows.size / 2

    for (i in matrixRows.indices) {
        if (i != middle) {
            print(padding)
        } else {
            print("$prefix = ")
        }

        println(matrixRows[i])
    }
}

private fun showSystem(matrix: Matrix, vector: Matrix) {
    println("Solving system: Mx = v")
    println()
    printMatrix("M", matrix)
    println()
    printMatrix("v", vector)
    println()
}

private fun asLMatrix(matrix: Matrix): Matrix {
    val new = matrix.copy()

    for (i in 0 until new.rows) {
        for (j in 0 until new.columns) {
            new[i, j] = when {
                i == j -> 1.0
                i < j -> 0.0
                else -> matrix[i, j]
            }
        }
    }

    return new
}

private fun asUMatrix(matrix: Matrix): Matrix {
    val new = matrix.copy()

    for (i in 0 until new.rows) {
        for (j in 0 until new.columns) {
            new[i, j] = when {
                i <= j -> matrix[i, j]
                else -> 0.0
            }
        }
    }

    return new
}

private fun luSolve(args: Array<String>) {
    val matrix = load(args[1])
    val vector = load(args[2])

    println("Using LU decomposition.")
    showSystem(matrix, vector)
    println("Performing LU decomposition...")

    val lu = MatrixOperations.luDecomposition(matrix)

    println()
    printMatrix("LU", lu)
    println()
    println("Performing forward substitution...")

    val y = MatrixOperations.forwardSubstitution(asLMatrix(lu), vector)

    println()
    printMatrix("y", y)
    println()
    println("Performing backward substitution...")

    val x = MatrixOperations.backwardSubstitution(asUMatrix(lu), y)

    printMatrix("x", x)
}

private fun getTolerance(tolerance: String): Double {
    try {
        val t = tolerance.toDouble()

        if (t < 0.0) {
            throw NumberFormatException()
        }

        return t
    } catch (e: NumberFormatException) {
        println("Tolerance must be a positive real number, but provided value was: $tolerance")
        exitProcess(1)
    }
}

private fun lupSolve(args: Array<String>) {
    val (tolerance, matrix, vector) = if (args.size == 4) {
        Triple(getTolerance(args[1]), load(args[2]), load(args[3]))
    } else {
        Triple(0.0, load(args[1]), load(args[2]))
    }

    println("Using LUP decomposition with tolerance of $tolerance.")
    showSystem(matrix, vector)
    println("Performing LUP decomposition...")

    val (lu, transformations) = MatrixOperations.lupDecomposition(matrix, tolerance)

    printMatrix("LU", lu)
    println()
    println("Swapping rows...")

    val swapped = transformations.apply(vector)

    printMatrix("vₛ", swapped)
    println()
    println("Performing forward substitution...")

    val y = MatrixOperations.forwardSubstitution(asLMatrix(lu), swapped)

    println()
    printMatrix("y", y)
    println()
    println("Performing backward substitution...")

    val x = MatrixOperations.backwardSubstitution(asUMatrix(lu), y)

    printMatrix("x", x)
}
