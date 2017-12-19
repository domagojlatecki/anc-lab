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
    val populationSize = properties.getProperty("populationSize").toInt()
    val maxIterations = properties.getProperty("maxIterations").toInt()
    val fitnessThreshold = properties.getProperty("fitnessThreshold").toDouble()
    val mutationChance = properties.getProperty("mutationChance").toDouble()
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
    val mutationOperator = if (chromosomeType == "double") {
        GaussianDoubleArrayMutator(mutationChance, mean, deviation)
    } else {
        BitFlipMutator(mutationChance)
    }

    @Suppress("UNCHECKED_CAST")
    fun runAlgorithm(functionString: String, numVariables: Int, function: (DoubleArray) -> Double) {
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
        }

        println()
    }

    runAlgorithm("100 · (x₂ - x₁²)² + (1 - x₁)²", 2, { x -> 100.0 * (x[1] - x[0].sq()).sq() + (1.0 - x[0]).sq() })
    runAlgorithm("∑ₖ (xₖ - k)²", 5, { x -> (0 until 5).map { (x[it] - (it + 1)).sq() }.sum() })
    runAlgorithm(
            "0.5 + (sin²(√∑ₖ (xₖ²)) - 0.5) / (1 + 0.001 · ∑ₖ (xₖ²))²",
            2,
            { x ->
                0.5 + (Math.sin(Math.sqrt(x[0].sq() + x[1].sq())).sq() - 0.5) /
                        (1 + 0.001 * (x[0].sq() + x[1].sq())).sq()
            }
    )
    runAlgorithm(
            "⁴√(∑ₖ (xₖ²)) · (1 + sin²(50 · ¹⁰√(∑ₖ (xₖ²))))",
            2,
            { x ->
                Math.pow(x[0].sq() + x[1].sq(), 0.25) * (1 + Math.sin(50.0 * Math.pow(x[0].sq() + x[1].sq(), 0.1)).sq())
            }
    )
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
