package day17

import Direction
import println
import readInput
import java.util.*

fun main() {
    fun processInput(input: List<String>): Array<Array<Int>> = input.map { line ->
        line.map { it.digitToInt() }.toTypedArray()
    }.toTypedArray()

    fun Direction.getNeighboring(): Set<Direction> = when (this) {
        Direction.UP, Direction.DOWN -> setOf(Direction.LEFT, Direction.RIGHT)
        Direction.LEFT, Direction.RIGHT -> setOf(Direction.UP, Direction.DOWN)
    }

    fun Direction.apply(pos: Pair<Int, Int>) = when (this) {
        Direction.UP -> pos.first - 1 to pos.second
        Direction.DOWN -> pos.first + 1 to pos.second
        Direction.LEFT -> pos.first to pos.second - 1
        Direction.RIGHT -> pos.first to pos.second + 1
    }

    fun Pair<Int, Int>.isInBounds(map: Array<Array<Int>>): Boolean = first in map.indices && second in map[0].indices

    data class State(val location: Pair<Int, Int>, val direction: Direction, val steps: Int)

    fun shortestPath(
        map: Array<Array<Int>>,
        start: Pair<Int, Int>,
        end: Pair<Int, Int>,
        minSteps: Int = 0,
        maxSteps: Int = 3
    ): Int {
        val visited = mutableSetOf<State>()
        val queue = PriorityQueue<Pair<State, Int>>(compareBy { it.second })

        queue.add(State(start, Direction.RIGHT, 0) to 0)

        while (queue.isNotEmpty()) {
            val (current, heatLoss) = queue.poll()
            if (visited.contains(current)) continue
            visited.add(current)

            if (current.location == end && current.steps >= minSteps) {
                return heatLoss
            }
            if (current.steps >= minSteps) {
                for (direction in current.direction.getNeighboring()) {
                    val newPos = direction.apply(current.location)
                    if (newPos.isInBounds(map)) {
                        val newState = State(newPos, direction, 1)
                        if (newState !in visited) {
                            queue.add(newState to heatLoss + map[newPos.first][newPos.second])
                        }
                    }
                }
            }
            if (current.steps < maxSteps) {
                val newPos = current.direction.apply(current.location)
                if (newPos.isInBounds(map)) {
                    val newState = State(newPos, current.direction, current.steps + 1)
                    if (newState !in visited) {
                        queue.add(newState to heatLoss + map[newPos.first][newPos.second])
                    }
                }
            }
        }
        throw IllegalStateException("No route to goal")
    }

    fun part1(input: Array<Array<Int>>): Int =
        shortestPath(input, 0 to 0, input.size - 1 to input[0].size - 1)

    fun part2(input: Array<Array<Int>>): Int =
        shortestPath(input, 0 to 0, input.size - 1 to input[0].size - 1, 4, 10)

// test if implementation meets criteria from the description, like:
    val testInput = readInput(17, true)
    check(part1(processInput(testInput)) == 102)

    val input = readInput(17)
    part1(processInput(input)).println()

    check(part2(processInput(testInput)) == 94)
    part2(processInput(input)).println()
}
