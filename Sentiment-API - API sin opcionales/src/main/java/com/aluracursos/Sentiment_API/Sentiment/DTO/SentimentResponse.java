package com.aluracursos.Sentiment_API.Sentiment.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SentimentResponse {
    private String prevision;
    private double probabilidad;
}
