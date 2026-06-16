package dev.freddiesilver.stocksim

import kotlin.test.Test
import kotlin.test.assertEquals

class EitherTest {
    @Test
    fun `Right holds success value`() {
        val either = success(42)
        assertEquals(42, either.value)
    }

    @Test
    fun `Left holds error value`() {
        val either = failure("error")
        assertEquals("error", either.value)
    }

    @Test
    fun `Right with null value`() {
        val either = success(null)
        assertEquals(null, either.value)
    }

    @Test
    fun `Left with different error types`() {
        val intError = failure(404)
        assertEquals(404, intError.value)

        val exceptionError = failure(RuntimeException("boom"))
        assertEquals("boom", exceptionError.value.message)
    }
}
