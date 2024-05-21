package day21

import println
import readInput
import java.util.*
import kotlin.math.pow


fun main() {
    data class Position(val x: Int, val y: Int)

    val directions = listOf(
        Position(-1, 0),
        Position(1, 0),
        Position(0, -1),
        Position(0, 1)
    )

    fun parseInput(input: List<String>): Triple<List<CharArray>, Int, Int> {
        val map = input.map { it.toCharArray() }

        val startX = map.indexOfFirst { it.contains('S') }
        val startY = map[startX].indexOf('S')

        return Triple(map, startX, startY)
    }

    fun part1(input: Triple<List<CharArray>, Int, Int>, maxSteps: Int): Int {
        val (map, startX, startY) = input
        val rows = map.size
        val cols = map[0].size

        val queue: Queue<Pair<Position, Int>> = LinkedList()
        queue.add(Position(startX, startY) to 0)

        val visited = mutableSetOf<Triple<Int, Int, Int>>()
        visited.add(Triple(startX, startY, 0))

        val reachablePositions = mutableSetOf<Pair<Int, Int>>()

        while (queue.isNotEmpty()) {
            val (current, steps) = queue.remove()

            if (steps == maxSteps) {
                reachablePositions.add(Pair(current.x, current.y))
                continue
            }

            for (dir in directions) {
                val newX = current.x + dir.x
                val newY = current.y + dir.y
                val newSteps = steps + 1

                if (newX in 0 until rows && newY in 0 until cols && map[newX][newY] != '#' && Triple(newX, newY, newSteps) !in visited) {
                    queue.add(Position(newX, newY) to newSteps)
                    visited.add(Triple(newX, newY, newSteps))
                }
            }
        }

        return reachablePositions.size
    }

    fun part2(input: Triple<List<CharArray>, Int, Int>, maxSteps: Int): Long {
        val (map, startX, startY) = input
        val rows = map.size
        val cols = map[0].size
        assert(rows == cols)

        val distances = mutableMapOf<Position, Int>()
        val queue = LinkedList<Pair<Position, Int>>()
        queue.add(Position(startX, startY) to 0)

        while (queue.isNotEmpty()) {
            val (current, steps) = queue.removeFirst()
            if (current in distances.keys) continue

            distances[current] = steps

            for (dir in directions) {
                val newX = current.x + dir.x
                val newY = current.y + dir.y
                val newSteps = steps + 1

                if (newX in 0 until rows && newY in 0 until cols && map[newX][newY] != '#' && Position(newX, newY) !in distances.keys) {
                    queue.add(Position(newX, newY) to newSteps)
                }
            }
        }

        val distToEdge = rows / 2
        val n = (maxSteps - distToEdge) / rows

        val oddTiles = (n + 1.0).pow(2.0).toLong()
        val evenTiles = n.toDouble().pow(2.0).toLong()

        val evenDistance = distances.filter { it.value % 2 == 0 }.count().toLong()
        val oddDistance = distances.filter { it.value % 2 == 1 }.count().toLong()

        val evenCorners = distances.filter { it.value > distToEdge && it.value % 2 == 0 }.count().toLong()
        val oddCorners = distances.filter { it.value > distToEdge && it.value % 2 == 1 }.count().toLong()

        return oddTiles * oddDistance + evenTiles * evenDistance - (n+1) * oddCorners + n * evenCorners
    }


// test if implementation meets criteria from the description, like:
    val testInput = readInput(21, true)
    check(part1(parseInput(testInput), 6) == 16)

    val input = readInput(21)
    part1(parseInput(input), 64).println()

    part2(parseInput(input), 26501365).println()
}
