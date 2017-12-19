package at.doml.anc.lab4.genalg.crossover

import java.util.Random

object ShortArrayUniformCrossover : CrossoverOperator<ShortArray> {

    private val random: Random = Random()

    override fun doCrossover(parent1: ShortArray, parent2: ShortArray): ShortArray {
        val childArray = ShortArray(parent1.size) { _ -> 0 }

        for (i in parent1.indices) {
            childArray[i] = if (random.nextBoolean()) parent1[i] else parent2[i]
        }

        return childArray
    }
}
