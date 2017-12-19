package at.doml.anc.lab4.genalg.mutation

import java.util.Random
import kotlin.experimental.or
import kotlin.experimental.xor


class BitFlipMutator(private val chance: Double) : MutationOperator<ShortArray> {

    private val random: Random = Random()

    override fun mutate(chromosome: ShortArray): ShortArray {
        val array = chromosome.clone()

        for (i in array.indices) {
            array[i] = array[i] xor generateBitMask()
        }

        return chromosome
    }

    private fun generateBitMask(): Short {
        var mask: Short = 0

        for (i in 0 until 16) {
            if (this.random.nextDouble() <= this.chance) {
                mask = mask or 1
            }

            mask = ((mask.toInt()) shl 1 and 0xFFFF).toShort()
        }

        return mask
    }
}
