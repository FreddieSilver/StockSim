package dev.freddiesilver.stocksim

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

fun errorResponse(message:String?, status: HttpStatus): ResponseEntity<*> =
    ResponseEntity.status(status).body(mapOf("error" to (message ?: "unknown")))
