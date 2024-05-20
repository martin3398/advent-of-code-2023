package day19

import println
import readInput


fun main() {
    data class Part(val x: Int, val m: Int, val a: Int, val s: Int) {
        fun getByString(varName: String): Int = when (varName) {
            "x" -> x
            "m" -> m
            "a" -> a
            "s" -> s
            else -> throw IllegalArgumentException("Invalid attribute: $varName")
        }
    }

    fun splitByOperator(operator: String, left: IntRange, right: Int): Pair<IntRange, IntRange> = when (operator) {
        ">" -> right + 1 .. left.last to left.first .. right
        "<" -> left.first..<right to right .. left.last
        else -> throw IllegalArgumentException("Invalid operator: $operator")
    }

    data class RangePart(val x: IntRange, val m: IntRange, val a: IntRange, val s: IntRange) {
        fun getByString(varName: String): IntRange = when (varName) {
            "x" -> x
            "m" -> m
            "a" -> a
            "s" -> s
            else -> throw IllegalArgumentException("Invalid attribute: $varName")
        }

        fun setByString(varName: String, value: IntRange) = when (varName) {
            "x" -> copy(x = value)
            "m" -> copy(m = value)
            "a" -> copy(a = value)
            "s" -> copy(s = value)
            else -> throw IllegalArgumentException("Invalid attribute: $varName")
        }

        fun count(): Long = x.count().toLong() * m.count().toLong() * a.count().toLong() * s.count().toLong()
    }

    fun evaluateOperator(operator: String, left: Int, right: Int): Boolean = when (operator) {
        ">" -> left > right
        "<" -> left < right
        else -> throw IllegalArgumentException("Invalid operator: $operator")
    }

    data class Rule(val condition: String?, val nextWorkflow: String) {
        fun check(part: Part): Boolean {
            if (condition == null) return true
            val operator = condition.filter { it == '<' || it == '>' || it == '=' }
            val parts = condition.split(operator)
            val attribute = parts[0]
            val value = parts[1].toInt()
            return evaluateOperator(operator, part.getByString(attribute), value)
        }

        fun split(part: RangePart): Pair<RangePart, RangePart?> {
            if (condition == null) return part to null

            val operator = condition.filter { it == '<' || it == '>' || it == '=' }
            val parts = condition.split(operator)
            val attribute = parts[0]
            val value = parts[1].toInt()

            val (new1, new2) = splitByOperator(operator, part.getByString(attribute), value)
            return part.setByString(attribute, new1) to part.setByString(attribute, new2)
        }
    }

    fun parseRule(ruleStr: String): Rule {
        val parts = ruleStr.split(":")
        return if (parts.size == 2) {
            Rule(parts[0], parts[1])
        } else {
            Rule(null, parts[0])
        }
    }

    fun parseWorkflow(workflowStr: String): Pair<String, List<Rule>> {
        val nameEndIndex = workflowStr.indexOf("{")
        val name = workflowStr.substring(0, nameEndIndex)
        val rulesStr = workflowStr.substring(nameEndIndex + 1, workflowStr.length - 1)
        val rules = rulesStr.split(",").map { parseRule(it) }
        return name to rules
    }

    fun parseInput(input: List<String>): Pair<Map<String, List<Rule>>, List<Part>> {
        val (workflowsRaw, partsRaw) = input.partition { it.contains(":") }
        val workflows = workflowsRaw.associate { parseWorkflow(it) }
        val parts = partsRaw.filter { it.isNotEmpty() }.map { partRaw ->
            val values = partRaw.removeSurrounding("{", "}").split(",").map {
                it.split("=")[1].toInt()
            }
            Part(values[0], values[1], values[2], values[3])
        }

        return workflows to parts
    }

    fun processPart(part: Part, workflows: Map<String, List<Rule>>): Boolean {
        var currentWorkflow = "in"
        while (true) {
            val rules = workflows[currentWorkflow] ?: return false
            for (rule in rules) {
                if (rule.check(part)) {
                    return when (rule.nextWorkflow) {
                        "A" -> true
                        "R" -> false
                        else -> {
                            currentWorkflow = rule.nextWorkflow
                            break
                        }
                    }
                }
            }
        }
    }

    fun part1(input: Pair<Map<String, List<Rule>>, List<Part>>): Int {
        val (workflows, parts) = input

        val acceptedParts = parts.filter { processPart(it, workflows) }
        val totalRatings = acceptedParts.sumOf { it.x + it.m + it.a + it.s }

        return totalRatings
    }

    fun part2(input: Pair<Map<String, List<Rule>>, List<Part>>): Long {
        val (workflows, _) = input

        val start = RangePart(1..4000, 1..4000, 1..4000, 1..4000)
        val queue = mutableListOf(start to "in")

        var accepted = 0L

        while (queue.isNotEmpty()) {
            val (part, workflow) = queue.removeAt(0)
            if (workflow == "A") {
                accepted += part.count()
                continue
            }
            if (workflow == "R") {
                continue
            }
            val rules = workflows[workflow]!!
            var cur = listOf(part)
            for (rule in rules) {
                val curNew = mutableListOf<RangePart>()
                for (p in cur) {
                    val (new1, new2) = rule.split(p)
                    queue.add(new1 to rule.nextWorkflow)
                    if (new2 != null) {
                        curNew.add(new2)
                    }
                }
                cur = curNew
            }
        }

        return accepted
    }

// test if implementation meets criteria from the description, like:
    val testInput = readInput(19, true)
    check(part1(parseInput(testInput)) == 19114)

    val input = readInput(19)
    part1(parseInput(input)).println()

    check(part2(parseInput(testInput)) == 167409079868000L)
    part2(parseInput(input)).println()
}
