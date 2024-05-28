package day23

import println
import readInput
import kotlin.math.max

typealias Coordinates = Pair<Int, Int>
typealias Node = Pair<Coordinates, Int>
typealias Grid = Array<Array<List<Node>>>

fun main() {
    fun parseInput1(input: List<String>): Grid {
        return input.mapIndexed { i, line ->
            line.mapIndexed { j, c ->
                when (c) {
                    '.' -> listOf(i - 1 to j, i + 1 to j, i to j - 1, i to j + 1)
                    '<' -> listOf(i to j - 1)
                    '>' -> listOf(i to j + 1)
                    '^' -> listOf(i - 1 to j)
                    'v' -> listOf(i + 1 to j)
                    else -> emptyList()
                }.filter { it.first in input.indices && it.second in line.indices }
                    .filter { input[it.first][it.second] != '#' }
                    .map { it to 1 }
            }.toTypedArray()
        }.toTypedArray()
    }

    fun parseInput2(input: List<String>): Grid {
        return input.mapIndexed { i, line ->
            line.mapIndexed { j, c ->
                when (c) {
                    '#' -> emptyList()
                    else -> listOf(i - 1 to j, i + 1 to j, i to j - 1, i to j + 1)
                }.filter { it.first in input.indices && it.second in line.indices }
                    .filter { input[it.first][it.second] != '#' }
                    .map { it to 1 }
            }.toTypedArray()
        }.toTypedArray()
    }

    fun prune(input: Grid): Grid {
        val junctions = mutableMapOf<Pair<Int, Int>, MutableList<Pair<Pair<Int, Int>, Int>>>()

        for (row in input.indices) {
            for (col in input[row].indices) {
                if (input[row][col].size != 2) {
                    junctions[row to col] = mutableListOf()
                }
            }
        }

        for (junction in junctions.keys) {
            var current = setOf(junction)
            val visited = mutableSetOf(junction)
            var distance = 0

            while (current.isNotEmpty()) {
                distance++
                current = buildSet {
                    for (c in current) {
                        input[c.first][c.second].filter { it.first !in visited }.forEach { n ->
                            if (n.first in junctions) {
                                junctions.getValue(junction).add(n.first to distance)
                            } else {
                                add(n.first)
                                visited.add(n.first)
                            }
                        }
                    }
                }
            }
        }

        return input.mapIndexed { i, row ->
            row.mapIndexed { j, c ->
                if (c.size == 2) {
                    c
                } else {
                    junctions.getValue(i to j)
                }
            }.toTypedArray()
        }.toTypedArray()
    }

    fun solve(input: Grid): Int {
        val grid = prune(input)
        var best = 0
        fun depthFirstSearch(
            node: Coordinates,
            path: MutableList<Node> = mutableListOf(),
            pathSet: MutableSet<Coordinates> = mutableSetOf()
        ) {
            if (node.first == grid.size - 1 && node.second == grid[node.first].size - 2) {
                best = max(best, path.sumOf { it.second })
                return
            }

            for (next in grid[node.first][node.second]) {
                if (next.first in pathSet) continue
                depthFirstSearch(next.first, path.also { it.add(next) }, pathSet.also { it.add(next.first) })
                path.remove(next)
                pathSet.remove(next.first)
            }
        }

        depthFirstSearch(0 to 1)

        return best
    }

// test if implementation meets criteria from the description, like:
    val testInput = readInput(23, true)
    check(solve(parseInput1(testInput)) == 94)

    val input = readInput(23)
    solve(parseInput1(input)).println()

    check(solve(parseInput2(testInput)) == 154)
    solve(parseInput2(input)).println()
}
