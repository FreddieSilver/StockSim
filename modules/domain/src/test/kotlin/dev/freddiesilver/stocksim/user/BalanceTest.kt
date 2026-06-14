package dev.freddiesilver.stocksim.user

import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class BalanceTest {

    @Test
    fun `balance with positive value is created successfully`() {
        val balance = Balance(BigDecimal("100.00"))
        assertEquals(BigDecimal("100.00"), balance.value)
    }

    @Test
    fun `balance with zero value is created successfully`() {
        val balance = Balance(BigDecimal.ZERO)
        assertEquals(BigDecimal.ZERO, balance.value)
    }

    @Test
    fun `negative balance throws exception`() {
        val exception = assertFailsWith<IllegalArgumentException> {
            Balance(BigDecimal("-1.00"))
        }
        assertTrue(exception.message!!.contains("negative"))
    }

}
