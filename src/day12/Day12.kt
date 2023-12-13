package day12

import println
import readInput
import java.util.*

fun main() {
    fun processInput(input: List<String>): List<Pair<String, List<Int>>> = input.map { line ->
        val (springs, arrangements) = line.split(" ")

        Pair(springs, arrangements.split(",").map { it.toInt() })
    }

    fun part1(input: List<Pair<String, List<Int>>>): Long = input.sumOf { instance ->
        val queue = PriorityQueue<Pair<String, List<Int>>>(compareBy { -it.first.length })
        val visited = mutableSetOf<Pair<String, List<Int>>>()
        val counts = mutableMapOf(Pair(instance, 1.toLong()))

        queue.add(instance)
        var totalCount: Long = 0

        while (queue.isNotEmpty()) {
            val (springs, arrangements) = queue.remove()
            val count: Long = counts[Pair(springs, arrangements)]!!
            if (!visited.add(Pair(springs, arrangements))) {
                continue
            }
            if (springs.isEmpty() && arrangements.isEmpty()) {
                totalCount += count
                continue
            }

            if (springs.startsWith(".") || springs.startsWith('?')) {
                val nextSequence = Pair(springs.drop(1), arrangements)
                queue.add(nextSequence)
                counts[nextSequence] = count + (counts[nextSequence] ?: 0)
            }

            if (arrangements.isNotEmpty() && (springs.startsWith('#') || springs.startsWith('?'))) {
                val length = arrangements.first()
                if (
                    springs.length >= length
                    && !springs.substring(0, length).any { it == '.' }
                    && (springs.length == length || listOf('.', '?').contains(springs[length]))
                ) {
                    val nextSequence = Pair(springs.drop(length + 1), arrangements.drop(1))
                    queue.add(nextSequence)
                    counts[nextSequence] = count + (counts[nextSequence] ?: 0)
                }
            }
        }

        totalCount
    }

    fun part2(input: List<Pair<String, List<Int>>>): Long = part1(input.map {
        Pair(
            it.first + "?" + it.first + "?" + it.first + "?" + it.first + "?" + it.first,
            listOf(it.second, it.second, it.second, it.second, it.second).flatten()
        )
    })

// test if implementation meets criteria from the description, like:
    val testInput = readInput(12, true)
    check(part1(processInput(testInput)) == 21.toLong())

    val input = readInput(12)
    part1(processInput(input)).println()

    check(part2(processInput(testInput)) == 525152.toLong())
    part2(processInput(input)).println()
}
