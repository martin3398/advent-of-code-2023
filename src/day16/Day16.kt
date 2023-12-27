package day16

import println
import readInput

enum class Direction {
    UP, DOWN, LEFT, RIGHT
}

fun main() {
    fun processInput(input: List<String>): Array<String> = input.toTypedArray()

    fun Direction.apply(x: Int, y: Int): Pair<Int, Int> = when (this) {
        Direction.UP -> x to y - 1
        Direction.DOWN -> x to y + 1
        Direction.LEFT -> x - 1 to y
        Direction.RIGHT -> x + 1 to y
    }

    fun getNextDirections(cell: Char, direction: Direction): List<Direction> {
        val directions = mutableListOf<Direction>()

        when (cell) {
            '.' -> directions.add(direction)
            '/' -> when (direction) {
                Direction.UP -> directions.add(Direction.RIGHT)
                Direction.DOWN -> directions.add(Direction.LEFT)
                Direction.LEFT -> directions.add(Direction.DOWN)
                Direction.RIGHT -> directions.add(Direction.UP)
            }

            '\\' -> when (direction) {
                Direction.UP -> directions.add(Direction.LEFT)
                Direction.DOWN -> directions.add(Direction.RIGHT)
                Direction.LEFT -> directions.add(Direction.UP)
                Direction.RIGHT -> directions.add(Direction.DOWN)
            }

            '|' -> when (direction) {
                Direction.UP -> directions.add(Direction.UP)
                Direction.DOWN -> directions.add(Direction.DOWN)
                Direction.LEFT -> directions.addAll(listOf(Direction.UP, Direction.DOWN))
                Direction.RIGHT -> directions.addAll(listOf(Direction.UP, Direction.DOWN))
            }

            '-' -> when (direction) {
                Direction.UP -> directions.addAll(listOf(Direction.LEFT, Direction.RIGHT))
                Direction.DOWN -> directions.addAll(listOf(Direction.LEFT, Direction.RIGHT))
                Direction.LEFT -> directions.add(Direction.LEFT)
                Direction.RIGHT -> directions.add(Direction.RIGHT)
            }
        }

        return directions
    }

    fun getNumberOfTiles(input: Array<String>, start: Triple<Int, Int, Direction>): Int {
        val queue = mutableListOf<Triple<Int, Int, Direction>>()
        val visited = mutableSetOf<Triple<Int, Int, Direction>>()
        queue.add(start)

        while (queue.isNotEmpty()) {
            val next = queue.removeFirst()
            val (x, y, direction) = next
            if (x < 0 || y < 0 || x >= input[0].length || y >= input.size) continue
            if (!visited.add(next)) continue

            val directionsToAdd = getNextDirections(input[y][x], direction)

            for (nextDirection in directionsToAdd) {
                val (newX, newY) = nextDirection.apply(x, y)
                queue.add(Triple(newX, newY, nextDirection))
            }
        }

        return visited.map { it.first to it.second }.toSet().size
    }

    fun part1(input: Array<String>): Int = getNumberOfTiles(input, Triple(0, 0, Direction.RIGHT))

    fun part2(input: Array<String>): Int {
        val candidates = mutableListOf<Triple<Int, Int, Direction>>()
        for (i in input.indices) {
            candidates.add(Triple(0, i, Direction.RIGHT))
            candidates.add(Triple(input[0].length - 1, i, Direction.RIGHT))
        }
        for (i in input[0].indices) {
            candidates.add(Triple(i, 0, Direction.DOWN))
            candidates.add(Triple(i, input.size - 1, Direction.UP))
        }

        return candidates.maxOf { getNumberOfTiles(input, it) }
    }

// test if implementation meets criteria from the description, like:
    val testInput = readInput(16, true)
    check(part1(processInput(testInput)) == 46)

    val input = readInput(16)
    part1(processInput(input)).println()

    check(part2(processInput(testInput)) == 51)
    part2(processInput(input)).println()
}
