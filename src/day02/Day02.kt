package day02

import readInput
import kotlin.math.max

fun main() {
    fun part1(input: List<String>): Int = input.sumOf {
        val (id, content) = it.split(": ")
        val rounds = content.split(";")
        if (rounds.all {
                val colorPattern = "(\\d+)\\s*(red|blue|green)".toRegex()
                val colors = mapOf("red" to 0, "blue" to 0, "green" to 0).toMutableMap()

                colorPattern.findAll(it).forEach { matchResult ->
                    val (number, color) = matchResult.destructured
                    colors[color] = number.toInt()
                }
                colors["red"]!! <= 12 && colors["blue"]!! <= 14 && colors["green"]!! <= 13
            }) id.removePrefix("Game ").toInt() else 0
    }

    fun part2(input: List<String>): Int = input.sumOf {
        val (_, content) = it.split(": ")
        val rounds = content.split(";")
        val colors = mapOf("red" to 0, "blue" to 0, "green" to 0).toMutableMap()
        rounds.forEach {
            val colorPattern = "(\\d+)\\s*(red|blue|green)".toRegex()

            colorPattern.findAll(it).forEach { matchResult ->
                val (number, color) = matchResult.destructured
                colors[color] = max(colors[color]!!, number.toInt())
            }
        }
        colors["red"]!! * colors["blue"]!! * colors["green"]!!
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput(2, true)
    check(part1(testInput) == 8)

    val input = readInput(2)
    println(part1(input))

    check(part2(testInput) == 2286)
    println(part2(input))
}
