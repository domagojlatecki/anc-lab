package at.doml.anc.lab4.genalg

import at.doml.anc.lab4.genalg.chromosome.Chromosome
import at.doml.anc.lab4.genalg.crossover.CrossoverOperator
import at.doml.anc.lab4.genalg.mutation.MutationOperator
import java.util.Collections

class GeneticAlgorithm<T>(private val populationSize: Int = 100,
                          private val maxIterations: Int = 10000,
                          private val fitnessThreshold: Double = 0.0,
                          private val verbose: Boolean = false,
                          private val crossoverOperator: CrossoverOperator<T>,
                          private val mutationOperator: MutationOperator<T>,
                          private val chromosomeFormatter: (Chromosome<T>) -> String = { it.toString() },
                          private val tournamentSize: Int = 3) {

    operator fun invoke(chromosomeGenerator: () -> T, evaluator: FitnessEvaluator<T>): Chromosome<T> {
        var iteration = 0
        var population = (0 until this.populationSize)
                .map { _ -> chromosomeGenerator() }
                .map { Chromosome(evaluator.evaluate(it), it) }
                .sorted()
        var best = population.first()

        printIfVerbose("Initial", best)

        while (iteration < this.maxIterations && best.fitness > this.fitnessThreshold) {
            iteration += 1

            val (selected, rest) = tournamentSelection(population)
            val sortedSelected = selected.sorted()
            val parents = sortedSelected.take(2)
            val child = this.crossoverOperator.doCrossover(parents.first().underlying, parents.last().underlying)
            val mutatedChild = this.mutationOperator.mutate(child)
            val evaluatedChild = Chromosome(evaluator.evaluate(mutatedChild), mutatedChild)

            population = (if (evaluatedChild <= sortedSelected.last()) {
                listOf(rest, sortedSelected.dropLast(1), listOf(evaluatedChild)).flatten()
            } else {
                listOf(rest, sortedSelected).flatten()
            }).sorted()

            if (population.first() <= best) {
                best = population.first()
                printIfVerbose("Iteration $iteration", best)
            }
        }

        return best
    }

    private fun tournamentSelection(population: List<Chromosome<T>>): Pair<List<Chromosome<T>>, List<Chromosome<T>>> {
        val mutable = population.toMutableList()
        Collections.shuffle(mutable)
        return Pair(mutable.take(this.tournamentSize), mutable.drop(this.tournamentSize))
    }

    private fun Chromosome<T>.formatted(): String = chromosomeFormatter(this)

    private fun printIfVerbose(prefix: String, best: Chromosome<T>) {
        if (this.verbose) {
            println("$prefix best: ${best.formatted()}, fitness: ${best.fitness}")
        }
    }
}
