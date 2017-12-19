package at.doml.anc.lab4.genalg.mutation

import java.util.Random

class GaussianDoubleArrayMutator(private val chance: Double,
                                 private val mean: Double,
                                 private val deviation: Double) : MutationOperator<DoubleArray> {

    private val random: Random = Random()

    override fun mutate(chromosome: DoubleArray): DoubleArray {
        val array = chromosome.clone()

        for (i in array.indices) {
            if (this.random.nextDouble() <= chance) {
                array[i] = array[i] + mean + random.nextGaussian() * deviation
            }
        }

        return array
    }
}
