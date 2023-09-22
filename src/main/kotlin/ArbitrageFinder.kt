class ArbitrageFinder(val exchangeRates: Array<DoubleArray>) {
    fun findArbitrage(): Arbitrage? {
//        val ratesByFromVertex: Map<Currency, ExchangeRate> =
//            exchangeRates.associateBy { it.exchange.from }
        return null
    }

    fun depthFirst(current: ExchangeRate, ratesByFromVertex: Map<Currency, ExchangeRate>) {

    }
}

data class Arbitrage(val exchanges: List<Exchange>) {
    constructor(vararg exchanges: Exchange) : this(exchanges.toList())
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

