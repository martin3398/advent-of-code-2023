package day08

import println
import readInput

fun main() {
    data class Node(val name: String, val right: String, val left: String)

    fun Node.isStart() = name.endsWith("A")
    fun Node.isEnd() = name.endsWith("Z")

    fun processInput(input: List<String>): Pair<String, Map<String, Node>> =
        Pair(input.first(), input.drop(2).associate {
            val (name, successors) = it.split(" = ")
            val (left, right) = successors.removePrefix("(").removeSuffix(")").split(", ")

            name to Node(name, right, left)
        })

    fun simulate(instructions: String, graph: Map<String, Node>, initialNode: Node): Long {
        var i = 0.toLong()
        var curNode = initialNode
        while (!curNode.isEnd()) {
            val instruction = instructions[(i % instructions.length).toInt()].also { i++ }
            curNode = when (instruction) {
                'L' -> graph[curNode.left]!!
                'R' -> graph[curNode.right]!!
                else -> throw IllegalStateException("Unknown instruction $instruction")
            }
        }

        return i
    }

    fun gcd(num1: Long, num2: Long): Long {
        var a = num1
        var b = num2
        while (b > 0) {
            b = (a % b).also { a = b }
        }
        return a
    }

    fun lcm(num1: Long, num2: Long): Long {
        return num1 * (num2 / gcd(num1, num2))
    }

    fun part1(input: Pair<String, Map<String, Node>>): Long =
        simulate(input.first, input.second, input.second["AAA"]!!)


    fun part2(input: Pair<String, Map<String, Node>>): Long {
        val (instructions, graph) = input
        val initialNodes = graph.values.filter { it.isStart() }.toSet()

        return initialNodes.map { simulate(instructions, graph, it) }.fold(1) { acc, i ->
            lcm(acc, i)
        }
    }


// test if implementation meets criteria from the description, like:
    val testInput1 = readInput(8, true)
    check(part1(processInput(testInput1)) == 6.toLong())

    val input = readInput(8)
    part1(processInput(input)).println()

    val testInput2 = readInput(8, test = true, secondPart = true)
    check(part2(processInput(testInput2)) == 6.toLong())
    part2(processInput(input)).println()
}
