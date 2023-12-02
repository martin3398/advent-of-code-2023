package day01

import readInput

fun main() {
    fun findDigits(mapping: Map<String, Int>) = { s: String ->
        var result = ""
        for (i in s.indices) {
            val prefix = s.substring(i, s.length)
            val candidates = mapping.keys.filter { prefix.startsWith(it) }
            if (candidates.isNotEmpty()) {
                result += mapping[candidates.first()].toString()
                break
            }
        }
        for (i in s.length - 1 downTo 0) {
            val suffix = s.substring(i, s.length)
            val candidates = mapping.keys.filter { suffix.startsWith(it) }
            if (candidates.isNotEmpty()) {
                result += mapping[candidates.first()].toString()
                break
            }
        }
        result.toInt()
    }

    fun part1(input: List<String>): Int = input.sumOf { s ->
        val digits = s.filter { it.isDigit() }
        ("" + digits.first() + digits.last()).toInt()
    }

    fun part2(input: List<String>): Int = input.sumOf(
        findDigits(
            mapOf(
                "zero" to 0,
                "one" to 1,
                "two" to 2,
                "three" to 3,
                "four" to 4,
                "five" to 5,
                "six" to 6,
                "seven" to 7,
                "eight" to 8,
                "nine" to 9,
                "0" to 0,
                "1" to 1,
                "2" to 2,
                "3" to 3,
                "4" to 4,
                "5" to 5,
                "6" to 6,
                "7" to 7,
                "8" to 8,
                "9" to 9
            )
        )
    )

    // test if implementation meets criteria from the description, like:
    val testInput1 = readInput(1, true)
    check(part1(testInput1) == 142)

    val input = readInput(1)
    println(part1(input))

    val testInput2 = readInput(1, true, true)
    check(part2(testInput2) == 281)
    println(part2(input))
}
