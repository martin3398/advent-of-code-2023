package day04

import readInput
import kotlin.math.pow

fun main() {
    fun getNumberOfMatching(input: String): Int {
        val (_, parts) = input.split(": ")
        val (winningStr, numbersStr) = parts.split(" | ")

        val process = { it: String ->
            it.split(" ").map { it.replace(" ", "") }.filter { it.isNotEmpty() }.toSet()
        }

        return process(winningStr).intersect(process(numbersStr)).size
    }

    fun part1(input: List<String>): Int = input.sumOf {
        val exponent = getNumberOfMatching(it)
        if (exponent == 0) 0 else 2.0.pow(exponent - 1).toInt()
    }

    fun part2(input: List<String>): Int {
        val winningNums = input.map { getNumberOfMatching(it) }
        val counts = input.map { 1 }.toIntArray()

        for (i in input.indices) {
            val wins = winningNums[i]
            for (j in 1..wins) {
                counts[i + j] += counts[i]
            }
        }

        return counts.sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput(4, true)
    check(part1(testInput) == 13)

    val input = readInput(4)
    println(part1(input))

    check(part2(testInput) == 30)
    println(part2(input))
}
