package com.aluracursos.Sentiment_API.Sentiment.DTO;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SentimentRequest {
    @NotBlank(message = "El campo 'text' no puede estar vacío")
    @Size(min = 10, message = "El texto debe tener al menos 10 caracteres")
    private String text;
}
