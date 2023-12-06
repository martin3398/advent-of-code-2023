package day05

import println
import readInput


typealias Range = Pair<Long, Long>
typealias Mapping = Triple<Long, Long, Long>

fun main() {
    fun applyMapping(input: Long, map: List<Mapping>): Long {
        for ((destStart, sourceStart, len) in map) {
            if (input in sourceStart..sourceStart + len) {
                return destStart + input - sourceStart
            }
        }

        return input
    }

    fun Range.intersect(other: Range): Range? {
        val start = this.first
        val end = this.first + this.second
        val otherStart = other.first
        val otherEnd = other.first + other.second

        val intersectionStart = maxOf(start, otherStart)
        val intersectionEnd = minOf(end, otherEnd)

        return if (intersectionStart <= intersectionEnd) {
            Pair(intersectionStart, intersectionEnd - intersectionStart)
        } else {
            null
        }
    }

    fun Mapping.toRange() = Pair(this.second, this.third)

    fun Range.intersectAndApply(mapping: Mapping): Range? {
        val intersected = this.intersect(mapping.toRange()) ?: return null

        return Pair(intersected.first + mapping.first - mapping.second, intersected.second)
    }

    fun Range.splitByAndApply(mapping: List<Mapping>): List<Range> {
        val mappingsPadded = mapping.toMutableList().also {
            if (it[0].second != 0L) {
                it.add(0, Triple(0, 0, it[0].second))
            }
            it.add(
                Triple(
                    it.last().second + it.last().third,
                    it.last().second + it.last().third,
                    10000000
                )
            )
        }

        return mappingsPadded.mapNotNull {
            this.intersectAndApply(it)
        }
    }

    fun getSeedRanges(input: String): List<Range> = input
        .removePrefix("seeds: ")
        .split(" ")
        .map { it.toLong() }
        .windowed(2, 2)
        .map { it[0] to it[1] }

    fun getMapping(input: List<String>): List<Mapping> = input.map { s ->
        val split = s.split(" ").map { it.toLong() }
        Triple(split[0], split[1], split[2])
    }.sortedBy { it.second }

    fun getMappings(input: List<String>) = input
        .drop(2)
        .filter { it.isEmpty() || it[0].isDigit() }
        .joinToString("\n")
        .split("\n\n")
        .map {
            getMapping(it.split("\n"))
        }

    fun part1(input: List<String>): Long {
        val seeds = input[0].removePrefix("seeds: ").split(" ").map { it.toLong() }
        val mappings = getMappings(input)

        return seeds.minOf { seed ->
            mappings.fold(seed) { cur, map ->
                applyMapping(cur, map)
            }
        }
    }

    fun part2(input: List<String>): Long {
        val seeds = getSeedRanges(input[0])
        val mappings = getMappings(input)


        return mappings.fold(seeds) { cur, map ->
            cur.flatMap { range ->
                range.splitByAndApply(map)
            }
        }.minOf { it.first }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput(5, true)
    check(part1(testInput) == 35.toLong())

    val input = readInput(5)
    part1(input).println()

    check(part2(testInput) == 46.toLong())
    part2(input).println()
}
