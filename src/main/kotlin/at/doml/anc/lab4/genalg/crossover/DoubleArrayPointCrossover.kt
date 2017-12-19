package at.doml.anc.lab4.genalg.crossover

import java.util.Random

object DoubleArrayPointCrossover : CrossoverOperator<DoubleArray> {

    private val random: Random = Random()

    override fun doCrossover(parent1: DoubleArray, parent2: DoubleArray): DoubleArray {
        val crossoverPoint = this.random.nextInt(parent1.size)
        val childArray = DoubleArray(parent1.size) { _ -> 0.0 }

        for (i in 0 until crossoverPoint) {
            childArray[i] = parent1[i]
        }

        for (i in crossoverPoint until parent1.size) {
            childArray[i] = parent2[i]
        }

        return childArray
    }
}
