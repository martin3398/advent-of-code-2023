package day15

import println
import readInput

fun main() {
    fun Array<MutableMap<String, Int>>.println() = this.forEachIndexed { i, map ->
        if (map.isNotEmpty()) {
            println("$i: $map")
        }
    }

    fun processInput(input: List<String>): List<String> = input.get(0).split(",")

    fun hash(input: String): Int {
        var hash = 0
        for (c in input) {
            hash += c.code
            hash *= 17
            hash %= 256
        }
        return hash
    }

    fun part1(input: List<String>): Int = input.sumOf { hash(it) }

    fun part2(input: List<String>): Int {
        val boxes = Array<MutableMap<String, Int>>(256) { mutableMapOf() }
        for (e in input) {
            if (e.endsWith('-')) {
                val key = e.dropLast(1)
                boxes[hash(key)].remove(key)
            } else if (e.contains('=')) {
                val (key, value) = e.split('=')
                boxes[hash(key)][key] = value.toInt()
            } else {
                throw Exception("Invalid input")
            }
            println()
            boxes.println()
        }

        var result = 0
        for ((boxI, e) in boxes.withIndex()) {
            for ((slotI, entry) in e.toList().withIndex()) {
                result += (boxI + 1) * (slotI + 1) * entry.second
            }
        }
        return result
    }

// test if implementation meets criteria from the description, like:
    val testInput = readInput(15, true)
    check(part1(processInput(testInput)) == 1320)

    val input = readInput(15)
    part1(processInput(input)).println()

    check(part2(processInput(testInput)) == 145)
    part2(processInput(input)).println()
}
