package com.vjiki.music.controller

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration

@RestController
@RequestMapping("/api/v1/proxy")
@CrossOrigin(origins = ["*"])
class ProxyController {

    private val httpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(10))
        .build()

    @GetMapping("/image")
    fun proxyImage(@RequestParam fileId: String): ResponseEntity<ByteArray> {
        return try {
            // Construct Google Drive URL
            val driveUrl = "https://drive.google.com/uc?export=download&id=$fileId"
            
            // Fetch from Google Drive
            val request = HttpRequest.newBuilder()
                .uri(URI.create(driveUrl))
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build()

            val response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray())
            
            if (response.statusCode() == 200) {
                // Determine content type from response or default to image
                val contentType = response.headers().firstValue("Content-Type")
                    .orElse(MediaType.IMAGE_JPEG_VALUE)
                
                val headers = HttpHeaders()
                headers.contentType = MediaType.parseMediaType(contentType)
                headers.cacheControl = "public, max-age=3600" // Cache for 1 hour
                
                ResponseEntity.ok()
                    .headers(headers)
                    .body(response.body())
            } else {
                ResponseEntity.status(HttpStatus.BAD_GATEWAY).build()
            }
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    @GetMapping("/audio")
    fun proxyAudio(@RequestParam fileId: String): ResponseEntity<ByteArray> {
        return try {
            // Construct Google Drive URL
            val driveUrl = "https://drive.google.com/uc?export=download&id=$fileId"
            
            // Fetch from Google Drive
            val request = HttpRequest.newBuilder()
                .uri(URI.create(driveUrl))
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build()

            val response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray())
            
            if (response.statusCode() == 200) {
                // Determine content type from response or default to audio
                val contentType = response.headers().firstValue("Content-Type")
                    .orElse(MediaType.APPLICATION_OCTET_STREAM_VALUE)
                
                val headers = HttpHeaders()
                headers.contentType = MediaType.parseMediaType(contentType)
                headers.cacheControl = "public, max-age=3600" // Cache for 1 hour
                headers.set("Accept-Ranges", "bytes") // Enable range requests for audio streaming
                
                ResponseEntity.ok()
                    .headers(headers)
                    .body(response.body())
            } else {
                ResponseEntity.status(HttpStatus.BAD_GATEWAY).build()
            }
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }
}

