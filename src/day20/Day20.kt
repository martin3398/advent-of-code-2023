package day20

import println
import readInput
import java.util.*

enum class Pulse {
    HIGH, LOW
}

abstract class Module(val name: String) {
    val destinations = mutableListOf<Module>()

    abstract fun receivePulse(pulse: Pulse, sender: Module): List<Pair<Module, Pulse>>

    fun addDestination(module: Module) {
        destinations.add(module)
    }
}

class FlipFlopModule(name: String) : Module(name) {
    private var state = false

    override fun receivePulse(pulse: Pulse, sender: Module): List<Pair<Module, Pulse>> {
        val result = mutableListOf<Pair<Module, Pulse>>()
        if (pulse == Pulse.LOW) {
            state = !state
            val newPulse = if (state) Pulse.HIGH else Pulse.LOW
            destinations.forEach { result.add(it to newPulse) }
        }
        return result
    }
}

class ConjunctionModule(name: String) : Module(name) {
    val inputs = mutableMapOf<String, Pulse>()

    fun addInput(module: Module) {
        inputs[module.name] = Pulse.LOW
    }

    override fun receivePulse(pulse: Pulse, sender: Module): List<Pair<Module, Pulse>> {
        inputs[sender.name] = pulse
        val newPulse = if (inputs.values.all { it == Pulse.HIGH }) Pulse.LOW else Pulse.HIGH
        return destinations.map { it to newPulse }
    }
}

class BroadcasterModule(name: String) : Module(name) {
    override fun receivePulse(pulse: Pulse, sender: Module): List<Pair<Module, Pulse>> {
        return destinations.map { it to pulse }
    }
}

class NoopModule(name: String) : Module(name) {
    override fun receivePulse(pulse: Pulse, sender: Module): List<Pair<Module, Pulse>> {
        return emptyList()
    }
}

fun main() {

    fun parseInput(input: List<String>): Map<String, Module> {
        val modules = mutableMapOf<String, Module>()
        val destinations = mutableMapOf<Module, List<String>>()
        input.forEach { line ->
            val (moduleDesc, destinationsDesc) = line.split(" -> ")
            val modDestinations = destinationsDesc.split(", ").map { it.trim() }
            val module = when {
                moduleDesc.startsWith("%") -> FlipFlopModule(moduleDesc.substring(1))
                moduleDesc.startsWith("&") -> ConjunctionModule(moduleDesc.substring(1))
                moduleDesc == "broadcaster" -> BroadcasterModule(moduleDesc)
                else -> error("Unknown module type: $moduleDesc")
            }

            modules[module.name] = module
            destinations[module] = modDestinations
        }

        destinations.forEach { (module, destNames) ->
            destNames.forEach { destName ->
                var destModule = modules[destName]
                if (destModule == null) {
                    destModule = NoopModule(destName)
                    modules[destName] = destModule
                }
                module.addDestination(destModule)
                if (destModule is ConjunctionModule) {
                    destModule.addInput(module)
                }
            }
        }

        return modules
    }

    fun part1(input: Map<String, Module>): Int {
        val broadcaster = input["broadcaster"] ?: error("Broadcaster module not found")
        val pulses = mutableMapOf(Pulse.LOW to 0, Pulse.HIGH to 0)

        repeat(1000) {
            val pulseQueue: Queue<Triple<Module, Pulse, Module>> = LinkedList()
            pulseQueue.add(Triple(broadcaster, Pulse.LOW, broadcaster))
            while (pulseQueue.isNotEmpty()) {
                val (sender, pulse, destination) = pulseQueue.poll()
                pulses[pulse] = pulses[pulse]!! + 1

                destination.receivePulse(pulse, sender).forEach { (m, p) ->
                    pulseQueue.add(Triple(destination, p, m))
                }
            }
        }

        return pulses[Pulse.LOW]!! * pulses[Pulse.HIGH]!!
    }

    fun gcd(a: Long, b: Long): Long {
        if (b == 0L) return a
        return gcd(b, a % b)
    }

    fun lcm(a: Long, b: Long): Long {
        return a / gcd(a, b) * b
    }

    fun lcmOfList(numbers: Collection<Long>): Long {
        if (numbers.isEmpty()) return 1
        return numbers.reduce { acc, num -> lcm(acc, num) }
    }

    fun part2(input: Map<String, Module>): Long {
        val broadcaster = input["broadcaster"] ?: error("Broadcaster module not found")
        val rxModule = input["rx"] ?: error("Rx module not found")
        val rxConjunction = input.values.filter { it is ConjunctionModule && it.destinations.contains(rxModule) }
            .map { it as ConjunctionModule }
            .firstOrNull() ?: error("Rx conjunction module not found")
        val rxConjunctionInputs = rxConjunction.inputs.keys.map { input[it]!! }

        val resetTimes = mutableMapOf<Module, Long>()

        var i = 0
        while (true) {
            val pulseQueue: Queue<Triple<Module, Pulse, Module>> = LinkedList()
            pulseQueue.add(Triple(broadcaster, Pulse.LOW, broadcaster))
            while (pulseQueue.isNotEmpty()) {
                val (sender, pulse, destination) = pulseQueue.poll()
                if (rxConjunctionInputs.contains(sender) && pulse == Pulse.HIGH) {
                    resetTimes[sender] = (i + 1).toLong()
                }

                //println("${sender.name} -$pulse-> ${destination.name}")
                destination.receivePulse(pulse, sender).forEach { (m, p) ->
                    pulseQueue.add(Triple(destination, p, m))
                }
            }
            if (resetTimes.size == rxConjunctionInputs.size) {
                break
            }

            i++
        }

        return lcmOfList(resetTimes.values)
    }

// test if implementation meets criteria from the description, like:
    val testInput = readInput(20, true)
    check(part1(parseInput(testInput)) == 11687500)

    val input = readInput(20)
    part1(parseInput(input)).println()

    part2(parseInput(input)).println()
}
