package com.aluracursos.Sentiment_API.Sentiment.Controller;

import com.aluracursos.Sentiment_API.Sentiment.DTO.ErrorResponse;
import com.aluracursos.Sentiment_API.Sentiment.DTO.SentimentRequest;
import com.aluracursos.Sentiment_API.Sentiment.DTO.SentimentResponse;
import com.aluracursos.Sentiment_API.Sentiment.client.SentimentClient;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/sentiment")
@Slf4j
@RequiredArgsConstructor
public class SentimentController {

    private final SentimentClient sentimentClient;

    @PostMapping
    public Mono<ResponseEntity<?>> analyzeSentiment(
            @Valid @RequestBody SentimentRequest request) {
        log.info("Recibido texto: {}", request.getText());
        return sentimentClient.predict(request.getText())
                .<ResponseEntity<?>>map(r -> ResponseEntity.ok(r))
                .onErrorResume(e -> Mono.just(
                        ResponseEntity.status(502)
                                .body(new ErrorResponse("DS service unavailable",
                                        "No se pudo contactar al modelo."))));
}
}
