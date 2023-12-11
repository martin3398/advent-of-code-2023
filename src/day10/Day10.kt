package day10

import println
import readInput

enum class PIPE(val neighbors: List<Pair<Int, Int>>) {
    NORTH_SOUTH(listOf(Pair(-1, 0), Pair(1, 0))), EAST_WEST(listOf(Pair(0, -1), Pair(0, 1))), NORTH_EAST(
        listOf(
            Pair(
                -1,
                0
            ), Pair(0, 1)
        )
    ),
    NORTH_WEST(listOf(Pair(-1, 0), Pair(0, -1))), SOUTH_EAST(listOf(Pair(1, 0), Pair(0, 1))), SOUTH_WEST(
        listOf(
            Pair(
                1,
                0
            ), Pair(0, -1)
        )
    ),
    NONE(listOf());
}

fun PIPE.getNeighbor(i: Int, j: Int): List<Pair<Int, Int>> = this.neighbors.map { Pair(it.first + i, it.second + j) }

fun main() {

    fun processInput(input: List<String>): Pair<Array<Array<PIPE>>, Pair<Int, Int>> {
        var sI = -1
        var sJ = -1
        val map = input.mapIndexed { i, line ->
            line.mapIndexed { j, it ->
                when (it) {
                    '|' -> PIPE.NORTH_SOUTH
                    '-' -> PIPE.EAST_WEST
                    'L' -> PIPE.NORTH_EAST
                    'J' -> PIPE.NORTH_WEST
                    'F' -> PIPE.SOUTH_EAST
                    '7' -> PIPE.SOUTH_WEST
                    'S' -> {
                        sI = i
                        sJ = j

                        val north = i > 0 && listOf('|', 'F', '7').contains(input[i - 1][j])
                        val south = i < input.size - 1 && listOf('|', 'L', 'J').contains(input[i + 1][j])
                        val east = j < line.length - 1 && listOf('-', 'J', '7').contains(input[i][j + 1])
                        val west = j > 0 && listOf('-', 'L', 'F').contains(input[i][j - 1])

                        if (north && south) PIPE.NORTH_SOUTH
                        else if (east && west) PIPE.EAST_WEST
                        else if (north && east) PIPE.NORTH_EAST
                        else if (north && west) PIPE.NORTH_WEST
                        else if (south && east) PIPE.SOUTH_EAST
                        else if (south && west) PIPE.SOUTH_WEST
                        else throw Exception("Invalid input")
                    }

                    else -> PIPE.NONE
                }
            }.toTypedArray()
        }.toTypedArray()

        return Pair(map, Pair(sI, sJ))
    }

    fun getLoop(map: Array<Array<PIPE>>, start: Pair<Int, Int>): Set<Pair<Int, Int>> {
        val visited = mutableSetOf(start, map[start.first][start.second].getNeighbor(start.first, start.second)[0])
        var cur = start

        while (true) {
            val next = map[cur.first][cur.second].getNeighbor(cur.first, cur.second).filter { !visited.contains(it) }
            if (next.isEmpty()) break
            cur = next[0]
            visited.add(cur)
        }

        return visited
    }

    fun part1(input: Pair<Array<Array<PIPE>>, Pair<Int, Int>>): Int = getLoop(input.first, input.second).size / 2

    fun part2(input: Pair<Array<Array<PIPE>>, Pair<Int, Int>>): Int {
        val map = input.first
        val loop = getLoop(map, input.second)

        var count = 0
        for ((i, row) in map.withIndex()) {
            var isInside = false
            var last = PIPE.NONE
            for ((j, pipe) in row.withIndex()) {
                if (loop.contains(Pair(i, j))) {
                    if (
                        pipe != PIPE.EAST_WEST
                        && !(last == PIPE.NORTH_EAST && pipe == PIPE.SOUTH_WEST)
                        && !(last == PIPE.SOUTH_EAST && pipe == PIPE.NORTH_WEST)
                    ) {
                        isInside = !isInside
                        last = pipe
                    }
                } else if (isInside) {
                    count++
                }
            }
        }

        return count
    }

// test if implementation meets criteria from the description, like:
    val testInput1 = readInput(10, true)
    check(part1(processInput(testInput1)) == 8)

    val input = readInput(10)
    part1(processInput(input)).println()

    val testInput2 = readInput(10, test = true, secondPart = true)
    check(part2(processInput(testInput2)) == 10)
    part2(processInput(input)).println()
}
