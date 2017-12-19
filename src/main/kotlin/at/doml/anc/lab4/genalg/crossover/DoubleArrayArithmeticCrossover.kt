package at.doml.anc.lab4.genalg.crossover

object DoubleArrayArithmeticCrossover : CrossoverOperator<DoubleArray> {

    override fun doCrossover(parent1: DoubleArray, parent2: DoubleArray): DoubleArray {
        val childArray = DoubleArray(parent1.size) { _ -> 0.0 }

        for (i in parent1.indices) {
            childArray[i] = (parent1[i] + parent2[i]) / 2.0
        }

        return childArray
    }
}
