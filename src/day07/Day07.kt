package day07

import println
import readInput

fun main() {
    fun processInput(input: List<String>): List<Pair<String, Int>> = input.map {
        val (name, bid) = it.split(" ")
        name to bid.toInt()
    }

    fun getCardRank(card: Char, jValue: Int): Int = when (card) {
        'A' -> 14
        'K' -> 13
        'Q' -> 12
        'J' -> jValue
        'T' -> 10
        else -> card.digitToInt()
    }

    fun getRankedBidSum(input: List<Pair<String, Int>>, jValue: Int, getTypeRank: (String) -> Int): Int {
        var comparator = compareBy<Pair<String, Int>> { getTypeRank(it.first) }
        for (i in 0..5) {
            comparator = comparator.thenBy { getCardRank(it.first[i], jValue) }
        }

        return input.sortedWith(comparator)
            .mapIndexed { index, (_, bid) -> (index + 1) * bid }
            .sum()

    }

    fun part1(input: List<Pair<String, Int>>): Int = getRankedBidSum(input, 11) { hand ->
        val frequencyMap = hand.groupingBy { it }.eachCount()

        when {
            frequencyMap.containsValue(5) -> 6
            frequencyMap.containsValue(4) -> 5
            frequencyMap.containsValue(3) && frequencyMap.containsValue(2) -> 4
            frequencyMap.containsValue(3) -> 3
            frequencyMap.values.count { it == 2 } == 2 -> 2
            frequencyMap.containsValue(2) -> 1
            else -> 0
        }
    }

    fun part2(input: List<Pair<String, Int>>): Int = getRankedBidSum(input, 1) { hand ->
        val numJokers = hand.count { it == 'J' }
        var frequencyMap = hand.groupingBy { it }.eachCount().toMutableMap().also { it.remove('J') }.toMap()

        val twoPairs = (frequencyMap.values.count { it == 2 } == 2)
                || (frequencyMap.containsValue(2) && numJokers > 0)
        val fullHouse = (frequencyMap.containsValue(3) && frequencyMap.containsValue(2))
                || (numJokers == 1 && frequencyMap.containsValue(3))
                || (numJokers == 1 && frequencyMap.entries.count { it.value == 2 } == 2)
                || (numJokers == 2 && frequencyMap.containsValue(2))
        frequencyMap = frequencyMap
            .mapValues { (_, v) -> v + numJokers }

        when {
            numJokers == 5 || frequencyMap.containsValue(5) -> 6
            frequencyMap.containsValue(4) -> 5
            fullHouse -> 4
            frequencyMap.containsValue(3) -> 3
            twoPairs -> 2
            frequencyMap.containsValue(2) -> 1
            else -> 0
        }
    }


    // test if implementation meets criteria from the description, like:
    val testInput = readInput(7, true)
    check(part1(processInput(testInput)) == 6440)

    val input = readInput(7)
    part1(processInput(input)).println()

    check(part2(processInput(testInput)) == 5905)
    part2(processInput(input)).println()
}
