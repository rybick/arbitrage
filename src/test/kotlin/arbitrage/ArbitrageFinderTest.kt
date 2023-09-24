package arbitrage

import assertk.assertThat
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import kotlin.test.Test

// TODO understand why skipping one does not work
// create approach with iterative improving of best path between two vertices

abstract class ArbitrageFinderTest {
    private val XXX = 2.0 // invalid exchange place holder

    private val legend = arrayOf("EUR", "USD", "PLN", "GBP")

    @Test
    fun `only 0-profit input`() {
        // given
        val exchangeRates: Array<DoubleArray> =
            arrayOf( //       EUR, USD, PLN, GBP
                doubleArrayOf(XXX, 1.0, 1.0, 1.0), // EUR
                doubleArrayOf(1.0, XXX, 1.0, 1.0), // USD
                doubleArrayOf(1.0, 1.0, XXX, 1.0), // PLN
                doubleArrayOf(1.0, 1.0, 1.0, XXX)  // GBP
            )

        // when
        val arbitrage = subject().findArbitrage(exchangeRates, legend)

        // then
        assertThat(arbitrage).isNull()
    }

    @Test
    fun `only negative-profit available`() {
        // given
        val exchangeRates: Array<DoubleArray> =
            arrayOf( //       EUR, USD, PLN, GBP
                doubleArrayOf(XXX, 0.5, 0.4, 0.3), // EUR
                doubleArrayOf(1.1, XXX, 0.4, 0.3), // USD
                doubleArrayOf(1.1, 1.2, XXX, 0.3), // PLN
                doubleArrayOf(1.1, 1.2, 1.3, XXX)  // GBP
            )

        // when
        val arbitrage = subject().findArbitrage(exchangeRates, legend)

        // then
        assertThat(arbitrage).isNull()
    }


    @Test
    fun `only positive-profit available`() {
        // given
        val exchangeRates: Array<DoubleArray> =
            arrayOf( //       EUR, USD, PLN, GBP
                doubleArrayOf(XXX, 1.1, 1.1, 1.1), // EUR
                doubleArrayOf(1.1, XXX, 1.1, 1.1), // USD
                doubleArrayOf(1.1, 1.1, XXX, 1.1), // PLN
                doubleArrayOf(1.1, 1.1, 1.1, XXX)  // GBP
            )

        // when
        val arbitrage = subject().findArbitrage(exchangeRates, legend)

        // then
        assertThat(arbitrage).isNotNull()
    }

    @Test
    fun `positive profit EUR to USD and back`() {
        // given
        val exchangeRates: Array<DoubleArray> =
            arrayOf( //        EUR, USD, PLN, GBP
                doubleArrayOf( XXX, 1.07, 4.59, 0.87), // EUR
                doubleArrayOf(0.94,  XXX, 4.31, 0.82), // USD
                doubleArrayOf(0.22, 0.23,  XXX, 0.19), // PLN
                doubleArrayOf(1.15, 1.22, 5.28,  XXX)  // GBP
            )

        // when
        val arbitrage = subject().findArbitrage(exchangeRates, legend)

        // then
        assertThat(arbitrage).isNotNull()
    }

    @Test
    fun `positive profit EUR, USD, PLN, GBP, EUR`() {
        // given
        val exchangeRates: Array<DoubleArray> =
            arrayOf( //        EUR, USD,   PLN,  GBP
                doubleArrayOf( XXX, 1.07, 4.59, 0.87), // EUR
                doubleArrayOf(0.93,  XXX, 4.31, 0.82), // USD
                doubleArrayOf(0.21, 0.23,  XXX, 0.19), // PLN
                doubleArrayOf(1.14, 1.22, 5.28,  XXX)  // GBP
            )

        // when
        val arbitrage = subject().findArbitrage(exchangeRates, legend)

        // then
        assertThat(arbitrage).isNotNull()
    }

    @Test
    fun `profit USD, PLN, GBP, USD`() {
        // given
        val exchangeRates: Array<DoubleArray> =
            arrayOf( //       EUR,   USD,  PLN,  GBP
                doubleArrayOf( XXX, 1.00, 2.00, 1.00), // EUR
                doubleArrayOf(0.80,  XXX, 2.00, 1.00), // USD
                doubleArrayOf(0.45, 0.49,  XXX, 0.50), // PLN
                doubleArrayOf(1.00, 1.10, 2.00,  XXX)  // GBP
            )

        // when
        val arbitrage = subject().findArbitrage(exchangeRates, legend)

        // then
        assertThat(arbitrage).isNotNull()
    }

    //@Test
    fun `a big matrix with slightly negative outcome`() {
        // given
        val currencyValues = IntRange(0, 12)
        val exchangeRates: Array<DoubleArray> =
            currencyValues.map { iVal ->
                currencyValues.map { jVal ->
                    0.999999 * iVal.toDouble() / jVal.toDouble()
                }.toDoubleArray()
            }.toTypedArray()
        val legend = defaultLegend(currencyValues.count())

        // when
        val arbitrage = subject().findArbitrage(exchangeRates, legend)

        // then
        assertThat(arbitrage).isNull()
    }

    abstract fun subject(): ArbitrageFinder

    private fun defaultLegend(size: Int): Array<String> = (0..size).map { "C$it" }.toTypedArray()
}

class BruteForceArbitrageFinderTest : ArbitrageFinderTest() {
    override fun subject(): ArbitrageFinder = BruteForceArbitrageFinderAdapter()
}

class DynamicArbitrageFinderTest : ArbitrageFinderTest() {
    override fun subject(): ArbitrageFinder = DynamicArbitrageFinderAdapter()
}

//class SkippingDynamicArbitrageFinderTest : ArbitrageFinderTest() {
//    override fun subject(): ArbitrageFinder = SkippingDynamicArbitrageFinderAdapter()
//}