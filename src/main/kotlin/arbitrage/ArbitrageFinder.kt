package arbitrage

interface ArbitrageFinder {

    fun findArbitrage(exchangeRates: Array<DoubleArray>, legend: Array<String>): Arbitrage?
}


class Arbitrage(val value: Double, val path: List<Int>, val legend: Array<String>) {
    override fun toString(): String {
        return "arbitrage.Arbitrage(value=$value, path=${path.map { legend[it] }})"
    }
}