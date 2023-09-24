package arbitrage

class ArbitrageFinder(val exchangeRates: Array<DoubleArray>, val legend: Array<String>) {
    val visited = Array(exchangeRates.size) { false }
    var startVertex = 0
    val path: ArrayDeque<Int> = ArrayDeque(exchangeRates.size)
    var found = false

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

class Arbitrage(val value: Double, val path: List<Int>, val legend: Array<String>) {

    override fun toString(): String {
        return "arbitrage.Arbitrage(value=$value, path=${path.map { legend[it] }})"
    }

}

data class Exchange(val from: Currency, val to: Currency)

data class ExchangeRate(
    val exchange: Exchange,
    val rate: Double
) {
    companion object {
        fun from(pair: Pair<Pair<Currency, Currency>, Double>): ExchangeRate {
            return ExchangeRate(
                exchange = with(pair.first) { Exchange(first, second) },
                rate = pair.second
            )
        }
    }
}

