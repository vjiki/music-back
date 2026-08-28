package com.vjiki.music.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vjiki.music.support.AbstractIntegrationTest;

/**
 * Controller integration tests: everything {@link AbstractIntegrationTest} provides, plus MockMvc.
 */
@AutoConfigureMockMvc
abstract class AbstractControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;
}
