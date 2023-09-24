package arbitrage

/**
 * Performs depth 1-st search looking for all cycles ending at ANY point.
 * Multiplies all edge weights on the way (exchange rate).
 * Stores inbverse of multiplied values in an array for each vertex.
 * If the ending point of the cycle is not starting point than multiplies it with the stored inverse for end point
 *  to get value for this point (exclude the part that is not part of the cycle).
 * If after closing the cycle, the multiplied value is > 1.0, that's the arbitrage.
 * Repeats above for each possible starting point.
 *
 * time: (n-1)! (so not much better than brute force)
 * space with input: n^2
 * space without input: n (visitedDistanceInverses + stack)
 *
 * n - number of currencies
 */
class DynamicArbitrageFinder(val exchangeRates: Array<DoubleArray>, val legend: Array<String>) {
    private val visitedDistanceInverses = Array(exchangeRates.size) { UNVISITED }
    private val startVertex = 0
    private val path: ArrayDeque<Int> = ArrayDeque(exchangeRates.size)
    private var found = false

    fun findArbitrage(): Arbitrage? {
        val value = depthFirst(startVertex, 1.0)
        return if (found) {
            Arbitrage(value, path + startVertex, legend)
        } else {
            null
        }
    }

    fun depthFirst(currentVertex: Int, value: Double): Double {
        if (currentVertex == startVertex && path.size > 1) {
            if (value > 1.0) {
                found = true
            }
            return value
        }
        visit(currentVertex, value)
        for (iVertex in 0 until visitedDistanceInverses.size) {
            if (iVertex == currentVertex) {
                continue
            }
            val iVertexValue = value * exchangeRates[currentVertex][iVertex]
            if (isUnvisited(iVertex)) {
                val newValue = depthFirst(iVertex, iVertexValue)
                if (found) {
                    return newValue
                }
            } else {
                val newValue = iVertexValue * visitedDistanceInverses[iVertex]
                if (newValue > 1.0) {
                    found = true
                    return value
                }
            }
        }
        leave(currentVertex)
        return value
    }

    private fun visit(vertex: Int, value: Double) {
        visitedDistanceInverses[vertex] = 1.0 / value
        path.addLast(vertex)
    }

    private fun leave(vertex: Int) {
        visitedDistanceInverses[vertex] = UNVISITED
        path.removeLast()
    }

    private fun isUnvisited(vertex: Int): Boolean = visitedDistanceInverses[vertex] == UNVISITED

    companion object {
        const val UNVISITED = -1.0
    }
}

class DynamicArbitrageFinderAdapter: ArbitrageFinder {
    override fun findArbitrage(exchangeRates: Array<DoubleArray>, legend: Array<String>): Arbitrage? =
        DynamicArbitrageFinder(exchangeRates, legend).findArbitrage()
}

