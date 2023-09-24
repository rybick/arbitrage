package arbitrage

/**
 * Performs depth 1-st search looking for all cycles ending at starting point.
 * Multiplies all edge weights on the way (exchange rate).
 * If after closing the cycle, the multiplied value is > 1.0, that's the arbitrage.
 * Repeats above for each possible starting point.
 *
 * time: n!
 * space with input: n^2
 * space without input: n (visited + stack)
 *
 * n - number of currencies
 */
class BruteForceArbitrageFinder(val exchangeRates: Array<DoubleArray>, val legend: Array<String>) {
    private val visited = Array(exchangeRates.size) { false }
    private var startVertex = 0
    private val path: ArrayDeque<Int> = ArrayDeque(exchangeRates.size)
    private var found = false

    fun findArbitrage(): Arbitrage? {
        while (startVertex < exchangeRates.size) {
            val value = depthFirst(startVertex, 1.0)
            if (found) {
                return Arbitrage(value, path + startVertex, legend)
            }
            ++startVertex
        }
        return null
    }

    fun depthFirst(currentVertex: Int, value: Double): Double {
        if (currentVertex == startVertex && path.size > 1) {
            if (value > 1.0) {
                found = true
            }
            return value
        }
        visit(currentVertex)
        for (vertex in 0 until visited.size) {
            if (!visited[vertex] || (vertex == startVertex && currentVertex != startVertex)) {
                val newValue = depthFirst(vertex, value * exchangeRates[currentVertex][vertex])
                if (found) {
                    return newValue
                }
            }
        }
        leave(currentVertex)
        return value
    }

    fun visit(vertex: Int) {
        visited[vertex] = true
        path.addLast(vertex)
    }

    fun leave(vertex: Int) {
        visited[vertex] = false
        path.removeLast()
    }
}

class BruteForceArbitrageFinderAdapter: ArbitrageFinder {
    override fun findArbitrage(exchangeRates: Array<DoubleArray>, legend: Array<String>): Arbitrage? =
        BruteForceArbitrageFinder(exchangeRates, legend).findArbitrage()
}

