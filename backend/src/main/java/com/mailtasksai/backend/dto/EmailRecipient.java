package com.mailtasksai.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmailRecipient {
    private EmailAddress emailAddress;

    public String getAddress() {
        return emailAddress != null ? emailAddress.getAddress() : null;
    }
}