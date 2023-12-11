package day11

import println
import readInput
import kotlin.math.abs


fun main() {
    fun processInput(input: List<String>): List<String> = input

    fun List<String>.println() = forEach { println(it) }

    fun expand(input: List<String>): List<String> {
        val inputMutable = input.toMutableList()
        val toInsertY = inputMutable.mapIndexed { y, row -> Pair(y, row) }
            .filter { row -> row.second.all { it == '.' } }
            .map { it.first }

        toInsertY.reversed().forEach {
            inputMutable.add(it, ".".repeat(inputMutable[0].length))
        }

        val toInsertX = (0 until inputMutable[0].length).filter { x -> inputMutable.all { it[x] == '.' } }
        toInsertX.reversed().forEach {
            inputMutable.forEachIndexed { y, row ->
                inputMutable[y] = row.substring(0, it) + "." + row.substring(it)
            }
        }

        return inputMutable
    }

    fun part1(input: List<String>): Int {
        val expanded = expand(input)
        val galaxies = expanded.asSequence().mapIndexed { y, row ->
            row.mapIndexed { x, c ->
                Triple(y, x, c)
            }
        }.flatten().filter { it.third == '#' }.map { Pair(it.first, it.second) }.toMutableSet()

        var sum = 0
        for (i in galaxies.indices) {
            for (j in i until galaxies.size) {
                val (y1, x1) = galaxies.elementAt(i)
                val (y2, x2) = galaxies.elementAt(j)
                sum += abs(y1 - y2) + abs(x1 - x2)
            }
        }

        return sum
    }

    fun part2(input: List<String>): Int = -1

// test if implementation meets criteria from the description, like:
    val testInput = readInput(11, true)
    check(part1(processInput(testInput)) == 374)

    val input = readInput(11)
    part1(processInput(input)).println()

    check(part2(processInput(testInput)) == 1)
    part2(processInput(input)).println()
}
