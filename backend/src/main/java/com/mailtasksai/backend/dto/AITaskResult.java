package com.mailtasksai.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mailtasksai.backend.model.UrgenciaEnum;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AITaskResult {

    @JsonProperty("resumo_tarefa")
    private String resumoTarefa;

    @JsonProperty("urgencia")
    private UrgenciaEnum urgencia;

    @JsonProperty("categoria_sugerida")
    private String categoriaSugerida;

    @JsonProperty("confidence")
    private Double confidence;
}