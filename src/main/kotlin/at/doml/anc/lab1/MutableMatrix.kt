package at.doml.anc.lab1

import java.io.File
import java.util.Arrays

class MutableMatrix constructor(rows: Int, columns: Int) : Matrix {

    init {
        if (rows <= 0 || columns <= 0) {
            throw IllegalArgumentException("invalid matrix dimensions provided: [$rows, $columns]")
        }
    }

    private val elements: Array<DoubleArray> = Array(rows) {
        DoubleArray(columns) { 0.0 }
    }

    override val rows: Int = this.elements.size
    override val columns: Int = this.elements[0].size

    constructor(elements: Array<DoubleArray>) : this(elements.size, elements[0].size) {
        for (i in elements.indices) {
            if (elements[i].size != elements[0].size) {
                throw IllegalArgumentException(
                        "provided array has inconsistent column dimensions;"
                                + " expected dimension: ${elements[0].size}, actual dimension: ${elements[i].size}"
                                + " at row with index: $i"
                )
            }

            System.arraycopy(elements[i], 0, this.elements[i], 0, elements[0].size)
        }
    }

    override fun copy(): MutableMatrix = MutableMatrix(this.elements)

    override operator fun get(row: Int, column: Int): Double = this.elements[row][column]

    override operator fun get(row: Int): DoubleArray = this.elements[row].copyOf()

    override operator fun set(row: Int, column: Int, value: Double) {
        this.elements[row][column] = value
    }

    override fun newMatrix(rows: Int, columns: Int) = MutableMatrix(rows, columns)

    override fun toString(): String = this.elements.joinToString(
            separator = "\n",
            transform = { row ->
                row.joinToString(
                        separator = " ",
                        transform = Double::toString
                )
            }
    )

    override fun toPrettyString(): String {
        val stringMatrix = this.elements.map { row ->
            row.map { e -> e.toString() }
        }

        val longest = stringMatrix.map({ row ->
            row.map(String::length).max()!!
        }).max()!!

        val withPadding = stringMatrix.map { row ->
            row.map { e -> e.padStart(longest) }
        }

        val padding = "".padStart((longest + 1) * this.columns)
        val builder = StringBuilder("┌ ")
                .append(padding)
                .append("┐\n")

        for (i in 0 until this.rows) {
            builder.append("│ ")

            for (j in 0 until this.columns) {
                builder.append(withPadding[i][j])
                builder.append(' ')
            }

            builder.append("│\n")
        }

        builder.append("└ ")
                .append(padding)
                .append('┘')

        return builder.toString()
    }

    override fun toArrayString(): String = this.elements.map {
        it.joinToString(prefix = "[", postfix = "]", separator = ",")
    }.joinToString(prefix = "[", postfix = "]", separator = ",")

    override fun equals(other: Any?): Boolean = when (other) {
        is Matrix -> this.equals(other)
        else -> false
    }

    override fun hashCode(): Int {
        var result = Arrays.hashCode(elements)

        result = 31 * result + rows
        result = 31 * result + columns

        return result
    }

    companion object {

        fun fromFile(file: File): MutableMatrix = this.parse(file.readLines())

        fun parse(string: String): MutableMatrix = this.parse(string.split("\n"))

        fun parse(lines: List<String>): MutableMatrix = MutableMatrix(
                lines.map(String::trim)
                        .filter(String::isNotEmpty)
                        .map({ line ->
                            line.split(Regex("\\s+"))
                                    .map(String::toDouble)
                                    .toDoubleArray()
                        }).toTypedArray()
        )
    }
}
