package com.aluracursos.Sentiment_API.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.aluracursos.Sentiment_API.Sentiment.DTO.SentimentRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SentimentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturn200WhenValidInput() throws Exception {
        SentimentRequest request = new SentimentRequest();
        request.setText("Me encanta este producto, es excelente");

        mockMvc.perform(post("/sentiment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.prevision").value("Positivo"))
                .andExpect(jsonPath("$.probabilidad").value(0.87));
    }

    @Test
    void shouldReturn400WhenTextTooShort() throws Exception {
        SentimentRequest request = new SentimentRequest();
        request.setText("short");

        mockMvc.perform(post("/sentiment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}