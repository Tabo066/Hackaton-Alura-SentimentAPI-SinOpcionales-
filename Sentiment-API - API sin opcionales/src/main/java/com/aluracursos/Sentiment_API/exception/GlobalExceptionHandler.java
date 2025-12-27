package com.aluracursos.Sentiment_API.exception;



import com.aluracursos.Sentiment_API.Sentiment.DTO.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /* 400 - entrada inválida */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String details = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.warn("Validation error: {}", details);
        return ResponseEntity.badRequest()
                .body(new ErrorResponse("Invalid input", details));
    }

    /* 502 - DS caído o timeout */
    @ExceptionHandler({java.net.ConnectException.class,
            java.util.concurrent.TimeoutException.class,
            WebClientResponseException.ServiceUnavailable.class})
    public ResponseEntity<ErrorResponse> handleDsUnavailable(Exception ex) {
        log.error("Data-Science service unavailable: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(new ErrorResponse("DS service unavailable",
                        "El servicio de predicción no está disponible. Intente más tarde."));
    }
}
