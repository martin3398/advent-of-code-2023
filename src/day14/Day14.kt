package day14

import println
import readInput

enum class DIRECTION {
    SOUTH, NORTH, WEST, EAST,
}

data class Day14Map(val map: Array<CharArray>) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Day14Map

        return map.contentDeepEquals(other.map)
    }

    override fun hashCode(): Int {
        return map.contentDeepHashCode()
    }

    fun copy(): Day14Map {
        return Day14Map(map.map { it.toTypedArray().toCharArray() }.toTypedArray())
    }
}

fun main() {
    fun processInput(input: List<String>): Day14Map = Day14Map(input.map { it.toCharArray() }.toTypedArray())

    fun move(input: Day14Map, direction: DIRECTION): Boolean {
        val map = input.map

        fun moveIfPossible(
            getNextPosition: (Int, Int) -> Pair<Int, Int>
        ): Boolean {
            var changed = false
            val rowRange = map.indices.let { if (direction == DIRECTION.SOUTH) it.reversed() else it }
            for (i in rowRange) {
                val colRange = map[i].indices.let { if (direction == DIRECTION.EAST) it.reversed() else it }
                for (j in colRange) {
                    val (nextI, nextJ) = getNextPosition(i, j)
                    if (nextI in map.indices && nextJ in map[i].indices && map[i][j] == 'O' && map[nextI][nextJ] == '.') {
                        map[i][j] = '.'
                        map[nextI][nextJ] = 'O'
                        changed = true
                    }
                }
            }
            return changed
        }

        return when (direction) {
            DIRECTION.NORTH -> moveIfPossible { i, j -> i - 1 to j }
            DIRECTION.SOUTH -> moveIfPossible { i, j -> i + 1 to j }
            DIRECTION.WEST -> moveIfPossible { i, j -> i to j - 1 }
            DIRECTION.EAST -> moveIfPossible { i, j -> i to j + 1 }
        }
    }

    fun getScore(input: Day14Map): Int {
        val map = input.map
        var score = 0
        for (i in map.indices) {
            for (j in map[i].indices) {
                if (map[i][j] == 'O') {
                    score += map.size - i
                }
            }
        }

        return score
    }

    fun repeatWhileTrue(action: () -> Boolean) {
        while (action()) {
            // Body not needed, as the action is performed in the condition
        }
    }


    fun part1(input: Day14Map): Int {
        repeatWhileTrue { move(input, DIRECTION.NORTH) }
        return getScore(input)
    }

    fun doStep(input: Day14Map) {
        repeatWhileTrue { move(input, DIRECTION.NORTH) }
        repeatWhileTrue { move(input, DIRECTION.WEST) }
        repeatWhileTrue { move(input, DIRECTION.SOUTH) }
        repeatWhileTrue { move(input, DIRECTION.EAST) }
    }

    fun part2(input: Day14Map, iterations: Int = 1000000000): Int {
        val buffer = mutableMapOf<Day14Map, Int>()
        var i = 0
        while (i < iterations) {
            doStep(input)
            i++

            val firstOccurrence = buffer.putIfAbsent(input.copy(), i)
            if (firstOccurrence != null) {
                val step = i - firstOccurrence
                i += (iterations - i) / step * step
            }
        }

        return getScore(input)
    }

// test if implementation meets criteria from the description, like:
    val testInput = readInput(14, true)
    check(part1(processInput(testInput)) == 136)

    val input = readInput(14)
    part1(processInput(input)).println()

    check(part2(processInput(testInput)) == 64)
    part2(processInput(input)).println()
}
