package arbitrage

import assertk.assertThat
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import kotlin.test.Test

internal class ArbitrageFinderTest {
    private val XXX = 2.0 // invalid value

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
        val arbitrage = ArbitrageFinder(exchangeRates, legend).findArbitrage()

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
        val arbitrage = ArbitrageFinder(exchangeRates, legend).findArbitrage()

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
        val arbitrage = ArbitrageFinder(exchangeRates, legend).findArbitrage()

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
        val arbitrage = ArbitrageFinder(exchangeRates, legend).findArbitrage()

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
        val arbitrage = ArbitrageFinder(exchangeRates, legend).findArbitrage()

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
        val arbitrage = ArbitrageFinder(exchangeRates, legend).findArbitrage()

        // then
        assertThat(arbitrage).isNotNull()
    }

    val USD = Currency("USD")
    val EUR = Currency("EUR")
    val PLN = Currency("PLN")
    val GBP = Currency("GBP")
    val CHF = Currency("CHF")
}
