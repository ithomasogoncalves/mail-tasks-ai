package com.mailtasksai.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mailtasksai.backend.model.UrgenciaEnum;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TaskRequest {

    @NotEmpty(message = "O título não pode estar vazio")
    private String title;

    @NotEmpty(message = "O destinatário não pode estar vazio")
    private String recipient;

    @NotEmpty(message = "A categoria não pode estar vazia")
    private String category;

    @NotNull(message = "A urgência é obrigatória")
    @JsonProperty("urgencia")
    private UrgenciaEnum urgencia;
}