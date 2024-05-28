package day25

import org.jgrapht.alg.StoerWagnerMinimumCut
import org.jgrapht.graph.DefaultWeightedEdge
import org.jgrapht.graph.SimpleWeightedGraph
import println
import readInput

fun main() {
    fun parseInput(input: List<String>): SimpleWeightedGraph<String, DefaultWeightedEdge> =
        SimpleWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge::class.java).apply {
            input.forEach { line ->
                val (name, others) = line.split(": ")
                this.addVertex(name)
                others.split(" ").forEach { other ->
                    this.addVertex(other)
                    this.addEdge(name, other)
                }
            }
        }

    fun part1(input: SimpleWeightedGraph<String, DefaultWeightedEdge>): Int {
        val oneSide = StoerWagnerMinimumCut(input).minCut()
        return (input.vertexSet().size - oneSide.size) * oneSide.size
    }


// test if implementation meets criteria from the description, like:
    val testInput = readInput(25, true)
    check(part1(parseInput(testInput)) == 54)

    val input = readInput(25)
    part1(parseInput(input)).println()
}
