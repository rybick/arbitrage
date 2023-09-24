package arbitrage

import java.lang.RuntimeException

class IterativeArrayArbitrageFinder(private val exchangeRates: Array<DoubleArray>, private val legend: Array<String>) {
    private val bestPaths: Array<Array<MutableList<Int>>> = Array(exchangeRates.size) {
        Array(exchangeRates.size) {
            ArrayList(exchangeRates.size)
        }
    }

    fun findArbitrage(): Arbitrage? {
        for (iterationNumber in 0..(exchangeRates.size+1)) {
            with(doIteration()) {
                if (arbitrage != null) {
                    return arbitrage
                }
                if (nothingChanged) {
                    return null // terminate sooner
                }
            }
        }
        throw RuntimeException("I think we are guaranteed to either find solution " +
                "or find there is no solution in n iterations. " +
                "Prove me wrong")
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
                            val before = bestPaths[fromVertex][toVertex].toMutableList()
                            val p1 = bestPaths[fromVertex][throughVertex]
                            val p2 = throughVertex
                            val p3 = bestPaths[throughVertex][toVertex]
                            clear()
                            addAll(bestPaths[fromVertex][throughVertex])
                            add(throughVertex)
                            addAll(bestPaths[throughVertex][toVertex])
                            if (size >= 2 && this[0] == 0 && this[1] == 0) {
                                println("ddd")
                            }
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

        val nothingChanged get() = anythingChanged

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