package com.vjiki.music.controller;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/proxy")
@CrossOrigin(origins = "*")
public class ProxyController {

    private static final Logger log = LoggerFactory.getLogger(ProxyController.class);

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    @GetMapping("/image")
    public ResponseEntity<byte[]> proxyImage(@RequestParam String fileId) {
        try {
            HttpResponse<byte[]> response = fetchFromDrive(fileId);

            if (response.statusCode() >= 200 && response.statusCode() <= 299) {
                String contentType = response.headers()
                        .firstValue("Content-Type")
                        .orElse(MediaType.IMAGE_JPEG_VALUE);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.parseMediaType(contentType));
                headers.setCacheControl("public, max-age=3600");

                return ResponseEntity.ok().headers(headers).body(response.body());
            }

            log.warn("Proxy image error: Status code {} for fileId: {}", response.statusCode(), fileId);
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
        } catch (Exception e) {
            log.error("Proxy image exception for fileId: {}", fileId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/audio")
    public ResponseEntity<byte[]> proxyAudio(@RequestParam String fileId) {
        try {
            HttpResponse<byte[]> response = fetchFromDrive(fileId);

            if (response.statusCode() >= 200 && response.statusCode() <= 299) {
                String contentType = response.headers()
                        .firstValue("Content-Type")
                        .orElse(MediaType.APPLICATION_OCTET_STREAM_VALUE);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.parseMediaType(contentType));
                headers.setCacheControl("public, max-age=30");
                headers.set("Accept-Ranges", "bytes");

                return ResponseEntity.ok().headers(headers).body(response.body());
            }

            log.warn("Proxy audio error: Status code {} for fileId: {}", response.statusCode(), fileId);
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
        } catch (Exception e) {
            log.error("Proxy audio exception for fileId: {}", fileId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private HttpResponse<byte[]> fetchFromDrive(String fileId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://drive.google.com/uc?export=download&id=" + fileId))
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
    }
}
