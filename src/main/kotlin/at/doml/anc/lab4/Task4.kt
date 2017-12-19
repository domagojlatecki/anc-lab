package at.doml.anc.lab4

import at.doml.anc.lab4.genalg.ConversionEvaluator
import at.doml.anc.lab4.genalg.DoubleTo16BitsCoder
import at.doml.anc.lab4.genalg.GeneticAlgorithm
import at.doml.anc.lab4.genalg.SimpleEvaluator
import at.doml.anc.lab4.genalg.chromosome.Chromosome
import at.doml.anc.lab4.genalg.crossover.CrossoverOperator
import at.doml.anc.lab4.genalg.crossover.DoubleArrayArithmeticCrossover
import at.doml.anc.lab4.genalg.crossover.DoubleArrayPointCrossover
import at.doml.anc.lab4.genalg.crossover.ShortArrayPointCrossover
import at.doml.anc.lab4.genalg.crossover.ShortArrayUniformCrossover
import at.doml.anc.lab4.genalg.mutation.BitFlipMutator
import at.doml.anc.lab4.genalg.mutation.GaussianDoubleArrayMutator
import at.doml.anc.lab4.genalg.mutation.MutationOperator
import java.nio.file.Files
import java.nio.file.Paths
import java.util.Properties
import java.util.Random
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    if (args.size != 1) {
        println("Path to property file is expected.")
        exitProcess(1)
    }

    val properties = Properties()

    properties.load(Files.newInputStream(Paths.get(args[0])))

    val lowerBound = properties.getProperty("lowerBound").toDouble()
    val upperBound = properties.getProperty("upperBound").toDouble()
    val maxIterations = properties.getProperty("maxIterations").toInt()
    val fitnessThreshold = properties.getProperty("fitnessThreshold").toDouble()
    val mean = properties.getProperty("mean").toDouble()
    val deviation = properties.getProperty("deviation").toDouble()
    val verbose = properties.getProperty("verbose").toBoolean()
    val chromosomeType = properties.getProperty("chromosomeType")
    val crossover = properties.getProperty("crossover")
    val crossoverOperator = when (chromosomeType) {
        "double" -> getCrossoverOperatorForDouble(crossover)
        "byte" -> getCrossoverOperatorForByte(crossover)
        else -> {
            println("Unknown chromosome type: $chromosomeType, available types: {'double', 'byte'}")
            exitProcess(1)
        }
    }
    val converter = DoubleTo16BitsCoder(lowerBound, upperBound)

    var populationSize = 0
    var mutationOperator: MutationOperator<*> = DummyMutation

    fun prepareAlgorithm(ps: Int, mc: Double) {
        populationSize = ps
        mutationOperator = if (chromosomeType == "double") {
            GaussianDoubleArrayMutator(mc, mean, deviation)
        } else {
            BitFlipMutator(mc)
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun runAlgorithm(functionString: String, numVariables: Int, function: (DoubleArray) -> Double): Double {
        println("Function: $functionString")
        println()

        if (chromosomeType == "double") {
            val evaluator = SimpleEvaluator(function)
            val formatter: (Chromosome<DoubleArray>) -> String = {
                it.underlying.joinToString(prefix = "[", separator = ", ", postfix = "]")
            }
            val solution = GeneticAlgorithm(
                    populationSize = populationSize,
                    maxIterations = maxIterations,
                    fitnessThreshold = fitnessThreshold,
                    verbose = verbose,
                    crossoverOperator = crossoverOperator as CrossoverOperator<DoubleArray>,
                    mutationOperator = mutationOperator as MutationOperator<DoubleArray>,
                    chromosomeFormatter = formatter
            )({ randomDoubleArray(numVariables, lowerBound, upperBound) }, evaluator)

            println("Solution: ${formatter(solution)}, value: ${function(solution.underlying)}")
            println()

            return solution.fitness
        } else {
            val evaluator = ConversionEvaluator(converter, function)
            val formatter: (Chromosome<ShortArray>) -> String = {
                it.underlying.map { converter.decode(it) }.joinToString(prefix = "[", separator = ", ", postfix = "]")
            }
            val solution = GeneticAlgorithm(
                    populationSize = populationSize,
                    maxIterations = maxIterations,
                    fitnessThreshold = fitnessThreshold,
                    verbose = verbose,
                    crossoverOperator = crossoverOperator as CrossoverOperator<ShortArray>,
                    mutationOperator = mutationOperator as MutationOperator<ShortArray>,
                    chromosomeFormatter = formatter
            )({ randomShortArray(numVariables) }, evaluator)

            println(
                    "Solution: ${formatter(solution)}, value: " +
                            function(solution.underlying.map { converter.decode(it) }.toDoubleArray())
            )
            println()

            return solution.fitness
        }
    }

    val results: MutableMap<String, DoubleArray> = mutableMapOf()

    for (popSize in listOf(30, 50, 100, 200)) {
        for (mutChance in listOf(0.05, 0.1, 0.3, 0.6, 0.9)) {
            val key = "populationSize: $popSize, mutationChance: $mutChance"

            results[key] = DoubleArray(10) { _ -> 0.0 }
            prepareAlgorithm(popSize, mutChance)

            for (i in 0 until 10) {
                results[key]!![i] = runAlgorithm(
                        "0.5 + (sin²(√∑ₖ (xₖ²)) - 0.5) / (1 + 0.001 · ∑ₖ (xₖ²))²",
                        2,
                        { x ->
                            0.5 + (Math.sin(Math.sqrt(x[0].sq() + x[1].sq())).sq() - 0.5) /
                                    (1 + 0.001 * (x[0].sq() + x[1].sq())).sq()
                        }
                )
                println("------------------------------------------------------------------------")
            }

            results[key]!!.sort()
        }
    }

    results.forEach { k, v ->
        val median = (v[4] + v[5]) / 2.0
        println("$k -> median: $median")
    }
}

private fun Double.sq() = this * this

private val random: Random = Random()

private fun randomDoubleArray(numVariables: Int, lowerBound: Double, upperBound: Double): DoubleArray {
    return DoubleArray(numVariables) { _ ->
        lowerBound + random.nextDouble() * (upperBound - lowerBound)
    }
}

private fun randomShortArray(numVariables: Int) = ShortArray(numVariables) { _ -> random.nextInt().toShort() }

private fun getCrossoverOperatorForDouble(crossover: String): CrossoverOperator<DoubleArray> = when (crossover) {
    "uniform" -> DoubleArrayArithmeticCrossover
    "point" -> DoubleArrayPointCrossover
    else -> {
        println("Unknown crossover operator: $crossover, available operators: {'uniform', 'point'}")
        exitProcess(1)
    }
}

private fun getCrossoverOperatorForByte(crossover: String): CrossoverOperator<ShortArray> = when (crossover) {
    "uniform" -> ShortArrayUniformCrossover
    "point" -> ShortArrayPointCrossover
    else -> {
        println("Unknown crossover operator: $crossover, available operators: {'uniform', 'point'}")
        exitProcess(1)
    }
}
