package com.vjiki.music.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Health check endpoint for Render.com and other monitoring services.
 * This endpoint keeps the free tier instance awake by responding to health checks.
 */
@RestController
@RequestMapping
class HealthController {

    @GetMapping("/healthz")
    fun health(): ResponseEntity<Map<String, String>> {
        return ResponseEntity.ok(mapOf("status" to "UP"))
    }
}

