package dev.freddiesilver.stocksim.trading.holding

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class HoldingTest {
    @Test
    fun `holding is created with correct fields`() {
        val holding =
            Holding(
                id = 1L,
                userId = 10L,
                stockId = 20L,
                quantity = 100,
            )
        assertEquals(1L, holding.id)
        assertEquals(10L, holding.userId)
        assertEquals(20L, holding.stockId)
        assertEquals(100, holding.quantity)
    }

    @Test
    fun `holding with default id is created`() {
        val holding =
            Holding(
                userId = 10L,
                stockId = 20L,
                quantity = 50,
            )
        assertEquals(0L, holding.id)
    }

    @Test
    fun `holding with zero quantity is valid`() {
        val holding =
            Holding(
                userId = 10L,
                stockId = 20L,
                quantity = 0,
            )
        assertEquals(0, holding.quantity)
    }

    @Test
    fun `negative quantity throws exception`() {
        val exception =
            assertFailsWith<IllegalArgumentException> {
                Holding(userId = 10L, stockId = 20L, quantity = -1)
            }
        assertTrue(exception.message!!.contains("negative"))
    }

    @Test
    fun `addQuantity increases quantity`() {
        val holding = Holding(userId = 10L, stockId = 20L, quantity = 100)
        holding.addQuantity(50)
        assertEquals(150, holding.quantity)
    }

    @Test
    fun `addQuantity with zero throws exception`() {
        val holding = Holding(userId = 10L, stockId = 20L, quantity = 100)
        val exception =
            assertFailsWith<IllegalArgumentException> {
                holding.addQuantity(0)
            }
        assertTrue(exception.message!!.contains("positive"))
    }

    @Test
    fun `addQuantity with negative throws exception`() {
        val holding = Holding(userId = 10L, stockId = 20L, quantity = 100)
        assertFailsWith<IllegalArgumentException> {
            holding.addQuantity(-10)
        }
    }

    @Test
    fun `removeQuantity decreases quantity`() {
        val holding = Holding(userId = 10L, stockId = 20L, quantity = 100)
        holding.removeQuantity(30)
        assertEquals(70, holding.quantity)
    }

    @Test
    fun `removeQuantity to zero is valid`() {
        val holding = Holding(userId = 10L, stockId = 20L, quantity = 100)
        holding.removeQuantity(100)
        assertEquals(0, holding.quantity)
    }

    @Test
    fun `removeQuantity with zero throws exception`() {
        val holding = Holding(userId = 10L, stockId = 20L, quantity = 100)
        val exception =
            assertFailsWith<IllegalArgumentException> {
                holding.removeQuantity(0)
            }
        assertTrue(exception.message!!.contains("positive"))
    }

    @Test
    fun `removeQuantity with negative throws exception`() {
        val holding = Holding(userId = 10L, stockId = 20L, quantity = 100)
        assertFailsWith<IllegalArgumentException> {
            holding.removeQuantity(-5)
        }
    }

    @Test
    fun `removeQuantity more than owned throws exception`() {
        val holding = Holding(userId = 10L, stockId = 20L, quantity = 100)
        val exception =
            assertFailsWith<IllegalArgumentException> {
                holding.removeQuantity(101)
            }
        assertTrue(exception.message!!.contains("Cannot remove more than owned"))
    }
}
