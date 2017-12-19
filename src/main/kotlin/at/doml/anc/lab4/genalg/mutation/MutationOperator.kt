package at.doml.anc.lab4.genalg.mutation

interface MutationOperator<T> {

    fun mutate(chromosome: T): T
}
