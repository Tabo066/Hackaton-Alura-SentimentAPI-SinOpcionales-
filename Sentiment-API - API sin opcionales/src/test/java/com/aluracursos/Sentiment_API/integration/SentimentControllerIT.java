package com.aluracursos.Sentiment_API.integration;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = "ds.url=http://localhost:9561")
class SentimentControllerIT {

    @Autowired
    private MockMvc mockMvc;

    private WireMockServer wireMockServer;

    @BeforeEach
    void setUp() {
        wireMockServer = new WireMockServer(9561);
        wireMockServer.start();
        WireMock.configureFor("localhost", 9561);
    }

    @AfterEach
    void tearDown() {
        wireMockServer.stop();
    }

    @Test
    void whenDsReturnsOk_then200andPrediction() throws Exception {
        stubFor(post(urlEqualTo("/predict"))
                .withHeader("Content-Type", equalTo("application/json"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                { "prevision": "Positivo", "probabilidad": 0.91 }
                                """)));

        // 1. Stub de WireMock ──> simula al DS
        stubFor(post(urlEqualTo("/predict"))
                .withHeader("Content-Type", equalTo("application/json"))
                .withRequestBody(equalToJson("""
                { "text": "me encanta este producto" }
                """))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                        { "prevision": "Positivo", "probabilidad": 0.91 }
                        """)));

// 2. MockMvc ──> llama a TU endpoint
        mockMvc.perform(WireMock.post("/sentiment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        { "text": "me encanta este producto" }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.prevision").value("Positivo"))
                .andExpect(jsonPath("$.probabilidad").value(0.91));
}}