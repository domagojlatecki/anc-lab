package at.doml.anc.lab4.genalg.chromosome

class Chromosome<out T>(val fitness: Double, val underlying: T) : Comparable<Chromosome<*>> {

    override operator fun compareTo(other: Chromosome<*>): Int = this.fitness.compareTo(other.fitness)
}
