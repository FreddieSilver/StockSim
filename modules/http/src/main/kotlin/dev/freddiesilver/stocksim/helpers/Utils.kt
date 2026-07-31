package dev.freddiesilver.stocksim.helpers

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

fun errorResponse(
    message: String?,
    status: HttpStatus,
): ResponseEntity<*> = ResponseEntity.status(status).body(mapOf("error" to (message ?: "unknown")))

fun messageResponse(
    message: String?,
    status: HttpStatus,
): ResponseEntity<*> = ResponseEntity.status(status).body(mapOf("message" to (message ?: "unknown")))

fun <T> dataResponse(
    status: HttpStatus,
    data: T? = null,
    token: String? = null,
): ResponseEntity<T>{
    return if (data != null) {
        ResponseEntity.status(status).apply {
            token?.let { header("Authorization", "Bearer $it") }
        }.body(data)
    } else ResponseEntity.status(status).build()
}