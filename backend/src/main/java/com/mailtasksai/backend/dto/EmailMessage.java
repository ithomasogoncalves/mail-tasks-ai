package com.mailtasksai.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmailMessage {
    private String id;
    private String subject;
    private EmailMessageBody body;
    private EmailRecipient from;
    private LocalDateTime receivedDateTime;
}