package at.doml.anc.lab4.genalg.crossover

interface CrossoverOperator<T> {

    fun doCrossover(parent1: T, parent2: T): T
}
