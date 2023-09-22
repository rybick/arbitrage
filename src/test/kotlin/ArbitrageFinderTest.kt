import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import kotlin.test.Test

internal class ArbitrageFinderTest {
    private val XXX = 2.0 // invalid value

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
        val arbitrage = ArbitrageFinder(exchangeRates).findArbitrage()

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
        val arbitrage = ArbitrageFinder(exchangeRates).findArbitrage()

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
        val arbitrage = ArbitrageFinder(exchangeRates).findArbitrage()

        // then
        assertThat(arbitrage).isNotNull()
    }

//    @Test
//    fun `simple cycle (positive)`() {
//        // given
//        val exchangeRates: List<ExchangeRate> = listOf(
//            EUR to PLN to 4.00,
//            PLN to GBP to 1.00,
//            GBP to USD to 1.25,
//            USD to EUR to 1.00,
//            EUR to CHF to 0.75,
//        ).map(ExchangeRate::from)
//
//        // when
//        val arbitrage = ArbitrageFinder(exchangeRates).findArbitrage()
//
//        // then
//        assertThat(arbitrage)
//            .isNotNull()
//            .transform { it.exchanges }
//            .hasSize(4)
//    }


    fun `ddd`() {
        val exchangeRates: List<ExchangeRate> = listOf(
            USD to PLN to 3.8,
            PLN to USD to 0.25,
            PLN to EUR to 0.25,
            EUR to PLN to 4.0
        ).map(ExchangeRate::from)
    }

    val USD = Currency("USD")
    val EUR = Currency("EUR")
    val PLN = Currency("PLN")
    val GBP = Currency("GBP")
    val CHF = Currency("CHF")
}
