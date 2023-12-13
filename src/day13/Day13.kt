package day13

import day03.toInt
import println
import readInput

fun main() {
    fun processInput(input: List<String>): List<List<String>> =
        input.fold(mutableListOf(mutableListOf<String>())) { acc, line ->
            if (line.isBlank()) {
                if (acc.last().isNotEmpty()) acc.add(mutableListOf())
            } else {
                acc.last().add(line)
            }
            acc
        }.filter { it.isNotEmpty() }

    fun getWrongPartsByReflectingOverCol(input: List<String>, col: Int): Int {
        return (0..col).sumOf {
            input.sumOf { line ->
                val reflectedIndex = 2 * col + 1 - it
                if (col !in line.indices || reflectedIndex !in line.indices) {
                    0
                } else {
                    (line[it] != line[reflectedIndex]).toInt()
                }
            }
        }
    }

    fun getWrongPartsByReflectingOverRow(input: List<String>, row: Int): Int {
        val transposed = List(input[0].length) { col ->
            List(input.size) { row ->
                input[row][col]
            }.joinToString("")
        }

        return getWrongPartsByReflectingOverCol(transposed, row)
    }

    fun calculateResultSum(input: List<List<String>>, allowedMissing: Int) = input.sumOf { map ->
        var result =
            (0 until map[0].length - 1).firstOrNull { getWrongPartsByReflectingOverCol(map, it) == allowedMissing }
        if (result == null) {
            result = (map.indices
                .firstOrNull { getWrongPartsByReflectingOverRow(map, it) == allowedMissing }!! + 1) * 100
        } else {
            result++
        }

        result
    }

    fun part1(input: List<List<String>>) = calculateResultSum(input, 0)

    fun part2(input: List<List<String>>) = calculateResultSum(input, 1)

// test if implementation meets criteria from the description, like:
    val testInput = readInput(13, true)
    check(part1(processInput(testInput)) == 405)

    val input = readInput(13)
    part1(processInput(input)).println()

    check(part2(processInput(testInput)) == 400)
    part2(processInput(input)).println()
}
