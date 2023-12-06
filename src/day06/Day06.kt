package day06

import day03.toInt
import println
import readInput

fun main() {
    fun processInput1(input: List<String>): List<Pair<Long, Long>> {
        val times = input[0].removePrefix("Time:").split(" ").filter { it.isNotBlank() }.map { it.toLong() }
        val distances = input[1].removePrefix("Distance:").split(" ").filter { it.isNotBlank() }.map { it.toLong() }

        return times.zip(distances)
    }

    fun processInput2(input: List<String>): Pair<Long, Long> {
        val times = input[0].removePrefix("Time:").replace(" ", "").toLong()
        val distances = input[1].removePrefix("Distance:").replace(" ", "").toLong()

        return Pair(times, distances)
    }

    fun getTime(time: Long, holdingTime: Long): Long {
        return (time - holdingTime) * holdingTime
    }

    fun part1(input: List<Pair<Long, Long>>): Long = input.fold(1) { acc, (time, distance) ->
        acc * (0..time).sumOf { (getTime(time, it) > distance).toInt() }.toLong()
    }

    fun part2(input: Pair<Long, Long>): Long {
        val (time, distance) = input
        return (0..time).sumOf { (getTime(time, it) > distance).toInt() }.toLong()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput(6, true)
    check(part1(processInput1(testInput)) == 288.toLong())

    val input = readInput(6)
    part1(processInput1(input)).println()

    check(part2(processInput2(testInput)) == 71503.toLong())
    part2(processInput2(input)).println()
}
