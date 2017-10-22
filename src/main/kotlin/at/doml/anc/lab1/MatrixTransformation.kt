package at.doml.anc.lab1

class MatrixTransformation(rows: Int, columns: Int) {

    private val rowIndexes: IntArray = IntArray(rows) { i -> i }
    private val columnIndexes: IntArray = IntArray(columns) { i -> i }

    fun swapColumns(from: Int, to: Int) {
        columnIndexes[from] = to
        columnIndexes[to] = from
    }

    fun swapRows(from: Int, to: Int) {
        rowIndexes[from] = to
        rowIndexes[to] = from
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
