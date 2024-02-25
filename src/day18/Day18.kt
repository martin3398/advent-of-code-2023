package day18

import Direction
import println
import readInput
import kotlin.math.abs

fun main() {
    fun Direction.move(pos: Pair<Long, Long>, count: Int) = when (this) {
        Direction.UP -> pos.copy(first = pos.first - count)
        Direction.DOWN -> pos.copy(first = pos.first + count)
        Direction.LEFT -> pos.copy(second = pos.second - count)
        Direction.RIGHT -> pos.copy(second = pos.second + count)
    }

    fun parseDirection(input: String): Direction = when (input) {
        "U", "3" -> Direction.UP
        "D", "1" -> Direction.DOWN
        "L", "2" -> Direction.LEFT
        "R", "0" -> Direction.RIGHT
        else -> throw IllegalArgumentException("Invalid direction: $input")
    }

    fun processInput1(input: List<String>): List<Pair<Direction, Int>> = input.map { line ->
        val (direction, distance, _) = line.split(" ")
        Pair(parseDirection(direction), distance.toInt())
    }

    fun processInput2(input: List<String>): List<Pair<Direction, Int>> = input.map { line ->
        val (_, _, hexCode) = line.split(" ")
        Pair(parseDirection(hexCode[7].toString()), hexCode.substring(2..<hexCode.length - 2).toInt(16))
    }

    fun solve(input: List<Pair<Direction, Int>>): Long {
        var pos = 0L to 0L

        var fillingCount = 0L
        var trenchCount = 0L
        for ((direction, count) in input) {
            val newPos = direction.move(pos, count)
            fillingCount += (newPos.second - pos.second) * (newPos.first + pos.first)
            trenchCount += count
            pos = newPos
        }

        return (abs(fillingCount) / 2 + trenchCount / 2 + 1)
    }

// test if implementation meets criteria from the description, like:
    val testInput = readInput(18, true)
    check(solve(processInput1(testInput)) == 62L)

    val input = readInput(18)
    solve(processInput1(input)).println()

    check(solve(processInput2(testInput)) == 952408144115L)
    solve(processInput2(input)).println()
}
