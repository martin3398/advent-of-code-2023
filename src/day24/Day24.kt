package day24

import println
import readInput
import kotlin.math.abs


fun main() {
    data class Hailstone(val x: Double, val y: Double, val z: Double, val vx: Double, val vy: Double, val vz: Double)

    fun parseInput(input: List<String>): List<Hailstone> = input.map { line ->
        val parts = line.split("@")
        val positionPart = parts[0].trim()
        val velocityPart = parts[1].trim()

        val positions = positionPart.split(",").map { it.trim().toDouble() }
        val velocities = velocityPart.split(",").map { it.trim().toDouble() }
        Hailstone(
            x = positions[0],
            y = positions[1],
            z = positions[2],
            vx = velocities[0],
            vy = velocities[1],
            vz = velocities[2]
        )
    }

    fun part1(input: List<Hailstone>, minBound: Long = 200000000000000L, maxBound: Long = 400000000000000L): Int {
        var result = 0
        for (pointA in input) for (pointB in input) if (pointA != pointB) {
            val mA = pointA.vy / pointA.vx
            val mB = pointB.vy / pointB.vx
            val cA = pointA.y - mA * pointA.x
            val cB = pointB.y - mB * pointB.x

            if (mA == mB) {
                continue
            }

            val xPos = (cB - cA) / (mA - mB)
            val yPos = mA * xPos + cA

            if ((xPos < pointA.x && pointA.vx > 0) || (xPos > pointA.x && pointA.vx < 0) || (xPos < pointB.x && pointB.vx > 0) || (xPos > pointB.x && pointB.vx < 0)) {
                continue
            }
            if (minBound <= xPos && xPos <= maxBound && minBound <= yPos && yPos <= maxBound) {
                result++
            }
        }

        return result / 2
    }

    fun updatePotentialSet(
        potentialSet: MutableSet<Double>?,
        axisValueA: Double, axisValueB: Double,
        positionA: Double, positionB: Double
    ): MutableSet<Double>? {
        if (axisValueA == axisValueB && abs(axisValueA) > 100) {
            val newSet = mutableSetOf<Double>()
            val difference = positionB - positionA
            for (v in -1000..999) {
                val vDouble = v.toDouble()
                if (vDouble == axisValueA) continue
                if (difference % (vDouble - axisValueA) == 0.0) {
                    newSet.add(vDouble)
                }
            }
            return (potentialSet?.intersect(newSet) ?: newSet).toMutableSet()
        }
        return potentialSet
    }

    fun part2(input: List<Hailstone>): Long {
        var potentialXSet: MutableSet<Double>? = null
        var potentialYSet: MutableSet<Double>? = null
        var potentialZSet: MutableSet<Double>? = null

        for (pointA in input) for (pointB in input) if (pointA != pointB) {
            potentialXSet = updatePotentialSet(potentialXSet, pointA.vx, pointB.vx, pointA.x, pointB.x)
            potentialYSet = updatePotentialSet(potentialYSet, pointA.vy, pointB.vy, pointA.y, pointB.y)
            potentialZSet = updatePotentialSet(potentialZSet, pointA.vz, pointB.vz, pointA.z, pointB.z)
        }

        val resultingVX = potentialXSet?.firstOrNull()!!
        val resultingVY = potentialYSet?.firstOrNull()!!
        val resultingVZ = potentialZSet?.firstOrNull()!!

        val pointA = input[0]
        val pointB = input[1]

        val ma = (pointA.vy - resultingVY) / (pointA.vx - resultingVX)
        val mb = (pointB.vy - resultingVY) / (pointB.vx - resultingVX)
        val ca = pointA.y - (ma * pointA.x)
        val cb = pointB.y - (mb * pointB.x)
        val xPos = ((cb - ca) / (ma - mb))
        val yPos = (ma * xPos + ca)
        val time = ((xPos - pointA.x) / (pointA.vx - resultingVX))
        val zPos = pointA.z + (pointA.vz - resultingVZ) * time

        return (xPos+yPos+zPos).toLong()
    }

// test if implementation meets criteria from the description, like:
    val testInput = readInput(24, true)
    check(part1(parseInput(testInput), 7, 27) == 2)

    val input = readInput(24)
    part1(parseInput(input)).println()

    part2(parseInput(input)).println()
}
