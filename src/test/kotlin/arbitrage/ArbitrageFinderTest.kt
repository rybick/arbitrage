package arbitrage

import assertk.Assert
import assertk.all
import assertk.assertThat
import assertk.assertions.isBetween
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import kotlin.test.Test

abstract class ArbitrageFinderTest {
    private val XXX = 1.0 // invalid exchange place holder

    private val EUR = 0
    private val USD = 1
    private val PLN = 2
    private val GBP = 3
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
                doubleArrayOf( XXX, 1.07, 4.58, 0.82), // EUR
                doubleArrayOf(0.92,  XXX, 4.31, 0.77), // USD
                doubleArrayOf(0.20, 0.22,  XXX, 0.18), // PLN
                doubleArrayOf(1.21, 1.20, 5.27,  XXX)  // GBP
            )

        // when
        val arbitrage = subject().findArbitrage(exchangeRates, legend)

        // then
        assertThat(arbitrage).isNotNull().all {
            transform { it.value }.isBetween(1.0044, 1.0045)
            transform { it.path }.isSameArbitrageAs(EUR, USD, PLN, GBP, EUR)
        }

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

    //@Test run a couple of seconds for dynamic and minutes for brute force
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

    protected fun defaultLegend(size: Int): Array<String> = (0..size).map { "C$it" }.toTypedArray()
}

private fun Assert<List<Int>>.isSameArbitrageAs(vararg currenciesArray: Int) {
    val currencies = currenciesArray.toList()
    given {
        assertThat(it.first()).isEqualTo(it.last())
        assertThat(currencies.first()).isEqualTo(currencies.last())
        assertThat(currencies.size).isEqualTo(it.size)
        val size = it.size - 1
        val original = it.subList(0, size)
        val expected = currencies.subList(0, size)
        val startValueInExpected = expected[0]
        val startIndexInOriginal = original.indexOf(startValueInExpected)
        for (i in 0 until size) {
            val originalIndex = (startIndexInOriginal + i) % size
            assertThat(original[originalIndex]).isEqualTo(expected[i])
        }
    }
}

class BruteForceArbitrageFinderTest : ArbitrageFinderTest() {
    override fun subject(): ArbitrageFinder = BruteForceArbitrageFinderAdapter()
}

class DynamicArbitrageFinderTest : ArbitrageFinderTest() {
    override fun subject(): ArbitrageFinder = DynamicArbitrageFinderAdapter()
}

class IterativeArrayArbitrageFinderTest : ArbitrageFinderTest() {
    override fun subject(): ArbitrageFinder = IterativeArrayArbitrageFinderAdapter()

    @Test
    // takes less than 100ms for 300 currencies,
    // while brute force and dynamic ones take too long for 12 currencies
    fun `a REALLY big matrix with slightly negative outcome`() {
        // given
        val currencyValues = IntRange(0, 300)
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
}

//class SkippingDynamicArbitrageFinderTest : ArbitrageFinderTest() {
//    override fun subject(): ArbitrageFinder = SkippingDynamicArbitrageFinderAdapter()
//}