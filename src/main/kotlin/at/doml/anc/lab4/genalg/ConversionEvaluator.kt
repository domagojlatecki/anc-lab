package at.doml.anc.lab4.genalg

class ConversionEvaluator(private val converter: DoubleTo16BitsCoder,
                          private val function: (DoubleArray) -> Double) : FitnessEvaluator<ShortArray> {

    override fun evaluate(chromosome: ShortArray): Double = Math.abs(function(chromosome.decode()))

    private fun ShortArray.decode(): DoubleArray = this.map { converter.decode(it) }.toDoubleArray()
}
