package day03

import readInput

fun Boolean.toInt() = if (this) 1 else 0

fun main() {
    fun extractNumber(string: String, index: Int): Int {
        var startIndex = index
        var endIndex = index
        while (startIndex > 0 && string[startIndex - 1].isDigit()) {
            startIndex--
        }
        while (endIndex < string.length - 1 && string[endIndex + 1].isDigit()) {
            endIndex++
        }
        return string.substring(startIndex, endIndex + 1).toInt()
    }

    fun part1(input: List<String>): Int {
        val mask = Array(input.size) { BooleanArray(input[0].length) { false } }

        input.forEachIndexed { i, string ->
            string.forEachIndexed { j, char ->
                if (char != '.' && !char.isDigit()) {
                    mask[i][j] = true
                }
            }
        }

        var changed: Boolean
        do {
            changed = false
            input.forEachIndexed { i, string ->
                string.forEachIndexed { j, char ->
                    if (!mask[i][j] && char.isDigit() &&
                        (-1..1).any { di ->
                            (-1..1).any { dj ->
                                !(di == 0 && dj == 0) &&
                                        (i + di in input.indices && j + dj in input[0].indices) &&
                                        mask[i + di][j + dj]
                            }
                        }
                    ) {
                        changed = true
                        mask[i][j] = true
                    }
                }
            }
        } while (changed)

        input.forEachIndexed { i, string ->
            string.forEachIndexed { j, _ ->
                if (!input[i][j].isDigit()) {
                    mask[i][j] = false
                }
            }
        }

        val result = mutableListOf<String>()
        for ((stringIndex, string) in input.withIndex()) {
            val stringBuilder = StringBuilder()

            for ((charIndex, char) in string.withIndex()) {
                if (stringIndex < mask.size && charIndex < mask[stringIndex].size && mask[stringIndex][charIndex]) {
                    stringBuilder.append(char)
                } else {
                    result.add(stringBuilder.toString())
                    stringBuilder.clear()
                }
            }
            result.add(stringBuilder.toString())
        }

        return result.filter { it.isNotEmpty() }.sumOf { it.toInt() }
    }

    fun part2(input: List<String>): Long {
        var result: Long = 0
        for ((i, string) in input.withIndex()) {
            for ((j, char) in string.withIndex()) {
                if (i == 0 || i == input.size - 1) continue
                if (char == '*') {
                    val hasLeft = input[i][j - 1].isDigit()
                    val hasRight = input[i][j + 1].isDigit()
                    val hasUpperLeft = input[i - 1][j - 1].isDigit()
                    val hasUpperRight = input[i - 1][j + 1].isDigit()
                    val hasLowerLeft = input[i + 1][j - 1].isDigit()
                    val hasLowerRight = input[i + 1][j + 1].isDigit()
                    val oneUpper = input[i - 1][j].isDigit()
                    val oneLower = input[i + 1][j].isDigit()

                    var total = hasLeft.toInt() + hasRight.toInt() + hasUpperLeft.toInt() + hasUpperRight.toInt() + hasLowerLeft.toInt() + hasLowerRight.toInt()
                    if (hasUpperLeft && hasUpperRight && oneUpper) total--
                    if (hasLowerLeft && hasLowerRight && oneLower) total--
                    if (!hasLowerRight && !hasLowerLeft && oneLower) total++
                    if (!hasUpperRight && !hasUpperLeft && oneUpper) total++
                    if (total != 2) continue

                    var current = 1
                    if (hasLeft) current *= extractNumber(input[i], j - 1)
                    if (hasRight) current *= extractNumber(input[i], j + 1)
                    if (oneUpper) current *= extractNumber(input[i - 1], j)
                    if (oneLower) current *= extractNumber(input[i + 1], j)
                    if (hasUpperLeft && !oneUpper) current *= extractNumber(input[i - 1], j - 1)
                    if (hasUpperRight && !oneUpper) current *= extractNumber(input[i - 1], j + 1)
                    if (hasLowerLeft && !oneLower) current *= extractNumber(input[i + 1], j - 1)
                    if (hasLowerRight && !oneLower) current *= extractNumber(input[i + 1], j + 1)

                    result += current
                }
            }
        }

        return result
    }

// test if implementation meets criteria from the description, like:
    val testInput = readInput(3, true)
    check(part1(testInput) == 4361)

    val input = readInput(3)
    println(part1(input))

    check(part2(testInput) == 467835.toLong())
    println(part2(input))
}
