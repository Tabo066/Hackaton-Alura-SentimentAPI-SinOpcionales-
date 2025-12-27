package com.aluracursos.Sentiment_API.Sentiment.client;


import com.aluracursos.Sentiment_API.Sentiment.DTO.SentimentResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Component
@Slf4j
public class SentimentClient {

    private final WebClient webClient;

    public SentimentClient(@Value("${ds.url:http://localhost:8000}") String baseUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public Mono<SentimentResponse> predict(String text) {
        log.info("Consultando DS con texto: {}", text);
        return webClient.post()
                .uri("/predict")
                .bodyValue(new DsRequest(text))
                .retrieve()
                .bodyToMono(SentimentResponse.class)
                .timeout(Duration.ofSeconds(5))
                .retryWhen(Retry.max(1).filter(this::isRetryable))
                .doOnNext(r -> log.info("Respuesta DS: {}", r))
                .doOnError(e -> log.error("Error llamando DS", e));
    }

    private boolean isRetryable(Throwable throwable) {
        return throwable instanceof java.io.IOException
                || throwable instanceof org.springframework.web.reactive.function.client.WebClientResponseException;
    }

    private record DsRequest(String text) {}
}
