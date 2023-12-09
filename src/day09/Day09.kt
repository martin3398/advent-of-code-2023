package day09

import println
import readInput

fun main() {
    fun processInput(input: List<String>): List<List<Int>> = input.map { line -> line.split(" ").map { it.toInt() } }

    fun derivative(input: List<Int>): List<Int> = input.drop(1).zip(input).map { (a, b) -> a - b }

    fun part1(input: List<List<Int>>): Long = input.sumOf { line ->
        val derivatives = mutableListOf<List<Int>>()
        var cur = line
        while (!cur.all { it == 0 }) {
            derivatives += derivative(cur)
            cur = derivative(cur)
        }

        val lastValueSum = derivatives.sumOf { it.last() }
        (lastValueSum + line.last()).toLong()
    }

    fun part2(input: List<List<Int>>): Long = input.sumOf { line ->
        val derivatives = mutableListOf<List<Int>>()
        var cur = line
        while (!cur.all { it == 0 }) {
            derivatives += derivative(cur)
            cur = derivative(cur)
        }

        val firstValuesSum = derivatives.map { it.first() }.foldRight(0) { a, b -> a - b }
        (line.first() - firstValuesSum).toLong()
    }

// test if implementation meets criteria from the description, like:
    val testInput = readInput(9, true)
    check(part1(processInput(testInput)) == 114.toLong())

    val input = readInput(9)
    part1(processInput(input)).println()

    check(part2(processInput(testInput)) == 2.toLong())
    part2(processInput(input)).println()
}
