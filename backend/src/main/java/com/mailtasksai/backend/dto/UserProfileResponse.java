package com.mailtasksai.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class UserProfileResponse {
    private String id;
    private String name;
    private String email;
    private String role;

    @JsonProperty("microsoft_connected")
    private boolean microsoftConnected;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    private CompanyInfo company;

    @Data
    @AllArgsConstructor
    public static class CompanyInfo {
        private Long id;
        private String name;
    }
}