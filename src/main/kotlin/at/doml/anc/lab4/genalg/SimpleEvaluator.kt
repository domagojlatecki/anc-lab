package at.doml.anc.lab4.genalg

class SimpleEvaluator(private val function: (DoubleArray) -> Double) : FitnessEvaluator<DoubleArray> {

    override fun evaluate(chromosome: DoubleArray): Double = Math.abs(function(chromosome))
}
