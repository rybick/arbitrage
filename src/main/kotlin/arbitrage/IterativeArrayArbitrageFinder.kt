package arbitrage

import java.lang.RuntimeException

/**
 * Finds the best (best value) paths for each pair of vertices.
 * In first iteration for each edge (exchange) checks if it more value can be received via doing
 * the exchange via throughVertex currency rather than directly.
 * If so exchange rate that utilizes throughVertex is saved as a better one in place of the direct one
 * and bestPaths array is updated to include the information that exchange should be done via this vertex (currency).
 * the steps above are run for each possible throughVertex value.
 *
 * [striketrhough]Then the steps are repeated up to vertex number times until no changes in the array are made
 * or when we find an exchange from A to A (the same currency) that has exchange rate bigger than 1.0.[/striketrhough]
 * That was the 1st idea. Looks like it's enough to do one iteration.
 *
 *
 * (Worst) time complexity: n^4 = m^2 
 *  - n^2 for iterating through the whole array
 *  - *n for iterating trhough all throughVertices
 *  - *n for bulding the path wheneved new solution is found (There is possibly a space for improvement here to do it in O(1)
 *    and hence go down with the overall complexity to n^3 = m*log(m)
 * Space complexity: n^3 = m*log(m)
 *
 * n - number of currencies (vertices)
 * m - number of possible direct exchanges (edges)
 */
class IterativeArrayArbitrageFinder(private val exchangeRates: Array<DoubleArray>, private val legend: Array<String>) {
    private val bestPaths: Array<Array<MutableList<Int>>> = Array(exchangeRates.size) {
        Array(exchangeRates.size) {
            ArrayList(exchangeRates.size)
        }
    }

    fun findArbitrage(): Arbitrage? {
        return doIteration().arbitrage
    }

    fun doIteration(): IterationResult {
        var anythingChanged = false
        for (throughVertex in exchangeRates.indices) {
            for (fromVertex in exchangeRates.indices) {
                for (toVertex in exchangeRates.indices) {
                    val directRate = exchangeRates[fromVertex][toVertex]
                    val rateThrough =
                        exchangeRates[fromVertex][throughVertex] * exchangeRates[throughVertex][toVertex]
                    if (directRate < rateThrough) {
                        exchangeRates[fromVertex][toVertex] = rateThrough
                        with(bestPaths[fromVertex][toVertex]) {
                            clear()
                            addAll(bestPaths[fromVertex][throughVertex])
                            add(throughVertex)
                            addAll(bestPaths[throughVertex][toVertex])
                        }
                        anythingChanged = true
                    }
                    if (fromVertex == toVertex) {
                        val theValue = exchangeRates[fromVertex][toVertex]
                        if (theValue > 1.0) {
                            return IterationResult.found(Arbitrage(
                                value = theValue,
                                path = listOf(fromVertex) + bestPaths[fromVertex][toVertex] + toVertex,
                                legend = legend
                            ))
                        }
                    }
                }
            }
        }
        return IterationResult.notFound(anythingChanged)
    }

    class IterationResult private constructor(
        val arbitrage: Arbitrage?,
        private val anythingChanged: Boolean
    ) {

        val nothingChanged get() = !anythingChanged

        companion object {
            fun found(arbitrage: Arbitrage?) =
                IterationResult(arbitrage, true)

            fun notFound(anythingChanged: Boolean) =
                IterationResult(null, anythingChanged)
        }

    }

}

class IterativeArrayArbitrageFinderAdapter: ArbitrageFinder {
    override fun findArbitrage(exchangeRates: Array<DoubleArray>, legend: Array<String>): Arbitrage? =
        IterativeArrayArbitrageFinder(exchangeRates, legend).findArbitrage()
}
