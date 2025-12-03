package com.mailtasksai.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmailMessageBody {
    private String content;
    private String contentType; // Ex: "HTML" ou "Text"
}