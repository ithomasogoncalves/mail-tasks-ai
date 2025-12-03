package com.mailtasksai.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true) // Ignora campos que não mapeamos
public class GraphEmailResponse {
    private List<EmailMessage> value;
}