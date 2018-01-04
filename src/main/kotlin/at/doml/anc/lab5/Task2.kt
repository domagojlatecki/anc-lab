package at.doml.anc.lab5

import at.doml.anc.lab1.MutableMatrix

fun main(args: Array<String>) {
    println("Finding inverse of matrix:")

    val matrix = MutableMatrix(
            arrayOf(
                    doubleArrayOf(4.0, -5.0, -2.0),
                    doubleArrayOf(5.0, -6.0, -2.0),
                    doubleArrayOf(-8.0, 9.0, 3.0)
            )
    )

    println(matrix.toPrettyString())
    println()
    println("Inverse:")

    try {
        println(matrix.inverse().toPrettyString())
    } catch (_: RuntimeException) {
        println("None exists (matrix is singular).")
    }
}
