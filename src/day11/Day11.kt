package day11

import println
import readInput
import java.util.*


fun main() {
    fun processInput(input: List<String>): List<String> = input

    fun shortestDistances(input: List<String>, start: Pair<Int, Int>, additionalDist: Long): Map<Pair<Int, Int>, Long> {
        val toInsertX = (0 until input[0].length).filter { x -> input.all { it[x] == '.' } }
        val toInsertY = input.indices.filter { y -> input[y].all { it == '.' } }

        val distances = mutableMapOf<Pair<Int, Int>, Long>()
        val priorityQueue = PriorityQueue<Pair<Pair<Int, Int>, Long>>(compareBy { it.second })

        distances[start] = 0
        priorityQueue.add(Pair(start, 0))

        while (priorityQueue.isNotEmpty()) {
            val (current, currentDistance) = priorityQueue.poll()

            if (currentDistance > distances.getOrDefault(current, Long.MAX_VALUE)) continue

            for (neighbor in listOf(
                Pair(current.first + 1, current.second),
                Pair(current.first - 1, current.second),
                Pair(current.first, current.second + 1),
                Pair(current.first, current.second - 1)
            )) {
                if (neighbor.first !in input.indices || neighbor.second !in input[0].indices) continue

                val newDistance =
                    currentDistance + 1 + if (neighbor.first in toInsertY || neighbor.second in toInsertX) additionalDist else 0

                if (newDistance < distances.getOrDefault(neighbor, Long.MAX_VALUE)) {
                    distances[neighbor] = newDistance
                    priorityQueue.add(Pair(neighbor, newDistance))
                }
            }
        }

        return distances
    }

    fun calculateDay11(input: List<String>, additionalDist: Long): Long {
        val galaxies = input.asSequence().mapIndexed { y, row ->
            row.mapIndexed { x, c ->
                Triple(y, x, c)
            }
        }.flatten().filter { it.third == '#' }.map { Pair(it.first, it.second) }.toMutableSet()

        var sum: Long = 0
        for (i in galaxies.indices) {
            val distances = shortestDistances(input, galaxies.elementAt(i), additionalDist)
            for (j in i until galaxies.size) {
                sum += distances[galaxies.elementAt(j)]!!
            }
        }

        return sum
    }

    fun part1(input: List<String>): Long = calculateDay11(input, 2 - 1)

    fun part2(input: List<String>): Long = calculateDay11(input, 1000000 - 1)

// test if implementation meets criteria from the description, like:
    val testInput = readInput(11, true)
    check(part1(processInput(testInput)) == 374.toLong())

    val input = readInput(11)
    part1(processInput(input)).println()

    check(part2(processInput(testInput)) == 82000210.toLong())
    part2(processInput(input)).println()
}
