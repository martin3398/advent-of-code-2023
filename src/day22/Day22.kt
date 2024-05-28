package day22

import println
import readInput
import java.util.*
import kotlin.math.max


fun main() {
    data class Position(val x: Int, val y: Int, var z: Int)

    fun overlapRanges(start1: Int, end1: Int, start2: Int, end2: Int): Boolean {
        return !(end1 < start2 || end2 < start1)
    }

    data class Brick(val id: Char, var pos1: Position, var pos2: Position) {
        val bricksAbove = mutableSetOf<Brick>()
        val bricksBelow = mutableSetOf<Brick>()
        private var supporting: MutableSet<Brick>? = null
        private var supportedBy: MutableSet<Brick>? = null

        fun onGround() = pos1.z == 1 || pos2.z == 1
        fun highestPoint(): Int = maxOf(pos1.z, pos2.z)
        fun lowestPoint(): Int = minOf(pos1.z, pos2.z)
        fun isLevelBelow(other: Brick): Boolean = highestPoint() == other.lowestPoint() - 1

        fun overlaps(other: Brick): Triple<Boolean, Boolean, Boolean> {
            val (x1, y1, z1) = pos1
            val (x2, y2, z2) = pos2
            val xRange1 = minOf(x1, x2) to maxOf(x1, x2)
            val yRange1 = minOf(y1, y2) to maxOf(y1, y2)
            val zRange1 = minOf(z1, z2) to maxOf(z1, z2)

            val (x3, y3, z3) = other.pos1
            val (x4, y4, z4) = other.pos2
            val xRange2 = minOf(x3, x4) to maxOf(x3, x4)
            val yRange2 = minOf(y3, y4) to maxOf(y3, y4)
            val zRange2 = minOf(z3, z4) to maxOf(z3, z4)

            val overlapX = overlapRanges(xRange1.first, xRange1.second, xRange2.first, xRange2.second)
            val overlapY = overlapRanges(yRange1.first, yRange1.second, yRange2.first, yRange2.second)
            val overlapZ = overlapRanges(zRange1.first, zRange1.second, zRange2.first, zRange2.second)

            return Triple(overlapX, overlapY, overlapZ)
        }

        fun getSupportedBy(): MutableSet<Brick> {
            if (supportedBy == null) {
                supportedBy = bricksBelow.filter { it.isLevelBelow(this) }.toMutableSet()
            }
            return supportedBy ?: mutableSetOf()
        }

        fun getSupporting(): MutableSet<Brick> {
            if (supporting == null) {
                supporting = bricksAbove.filter { this.isLevelBelow(it) }.toMutableSet()
            }
            return supporting ?: mutableSetOf()
        }

        fun isUnder(other: Brick): Boolean {
            val (xOverlap, yOverlap, _) = overlaps(other)
            return xOverlap && yOverlap
        }

        fun drop(amount: Int = 1) {
            pos1 = pos1.copy(z = pos1.z - amount)
            pos2 = pos2.copy(z = pos2.z - amount)
        }
    }

    fun parseInput(input: List<String>): List<Brick> {
        return input.mapIndexed { index, line ->
            val parts = line.split("~")
            val start = parts[0].split(",").map { it.toInt() }
            val end = parts[1].split(",").map { it.toInt() }
            Brick(
                id = 'A' + index,
                pos1 = Position(start[0], start[1], start[2]),
                pos2 = Position(end[0], end[1], end[2])
            )
        }
    }

    fun drop(input: List<Brick>): List<Brick> {
        val bricks = input.map { it.copy() }.toMutableList()
        bricks.sortBy { it.lowestPoint() }

        for (falling in bricks) {
            if (falling.onGround()) continue

            var highestPoint = 1

            val lowerBricks = bricks.filter { it.lowestPoint() < falling.lowestPoint() }

            if (lowerBricks.isEmpty()) continue

            for (lower in lowerBricks) {
                if (lower.isUnder(falling)) {
                    falling.bricksBelow.add(lower)
                    lower.bricksAbove.add(falling)
                    highestPoint = max(highestPoint, lower.highestPoint() + 1)
                }
            }

            if (falling.lowestPoint() > highestPoint) {
                falling.drop(falling.lowestPoint() - highestPoint)
            }
        }

        bricks.sortBy { it.lowestPoint() }
        return bricks
    }

    fun part1(input: List<Brick>): Int {
        val bricks = drop(input)

        return bricks.sortedBy { it.lowestPoint() }.filter {
            !it.getSupporting().any { supporting -> supporting.getSupportedBy().size == 1 }
        }.size
    }

    fun part2(input: List<Brick>): Int {
        val bricks = drop(input)

        return bricks.sortedBy { it.lowestPoint() }.sumOf { brick ->
            val queue = LinkedList<Brick>()
            queue.add(brick)
            val disintegrated = mutableSetOf(brick.id)

            while (queue.isNotEmpty()) {
                val cur = queue.remove()
                for (above in cur.getSupporting()) {
                    if (disintegrated.contains(above.id)) continue

                    val supportsDisintegratedCount = above.getSupportedBy().count { disintegrated.contains(it.id) }

                    if (above.getSupportedBy().size == supportsDisintegratedCount) {
                        disintegrated.add(above.id)
                    }

                    queue.add(above)
                }
            }

            disintegrated.size - 1
        }
    }


// test if implementation meets criteria from the description, like:
    val testInput = readInput(22, true)
    check(part1(parseInput(testInput)) == 5)

    val input = readInput(22)
    part1(parseInput(input)).println()

    check(part2(parseInput(testInput)) == 7)
    part2(parseInput(input)).println()
}
