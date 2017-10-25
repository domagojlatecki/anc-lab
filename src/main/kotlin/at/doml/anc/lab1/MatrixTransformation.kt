package at.doml.anc.lab1

class MatrixTransformation(rows: Int, columns: Int) {

    private val rowIndexes: IntArray = IntArray(rows) { i -> i }
    private val columnIndexes: IntArray = IntArray(columns) { i -> i }

    fun swapColumns(from: Int, to: Int) {
        val newFrom = this.columnIndexes[to]
        val newTo = this.columnIndexes[from]

        columnIndexes[from] = newFrom
        columnIndexes[to] = newTo
    }

    fun swapRows(from: Int, to: Int) {
        val newFrom = this.rowIndexes[to]
        val newTo = this.rowIndexes[from]

        rowIndexes[from] = newFrom
        rowIndexes[to] = newTo
    }

    fun apply(matrix: Matrix): Matrix {
        val new = matrix.copy()

        for (i in 0 until matrix.rows) {
            for (j in 0 until matrix.columns) {
                new[i, j] = matrix[this.rowIndexes[i], this.columnIndexes[j]]
            }
        }

        return new
    }
}
