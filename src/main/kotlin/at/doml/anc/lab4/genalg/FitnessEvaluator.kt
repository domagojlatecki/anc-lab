package at.doml.anc.lab4.genalg

interface FitnessEvaluator<in T> {

    fun evaluate(chromosome: T): Double
}
