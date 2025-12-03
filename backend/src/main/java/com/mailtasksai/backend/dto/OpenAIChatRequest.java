package com.mailtasksai.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OpenAIChatRequest {
    private String model;
    private List<OpenAIMessage> messages;
    private double temperature;

    @JsonProperty("response_format")
    private Map<String, String> responseFormat;
}