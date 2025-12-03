package com.mailtasksai.backend.service;

import com.mailtasksai.backend.dto.EmailMessage;
import com.mailtasksai.backend.dto.GraphEmailResponse;
import com.mailtasksai.backend.model.CompanyTokens;
import com.mailtasksai.backend.util.EncryptionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class GraphApiClient {

    @Value("${graph.api.base-url}")
    private String graphApiBaseUrl;

    @Value("${azure.activedirectory.client-id}")
    private String clientId;
    @Value("${azure.activedirectory.client-secret}")
    private String clientSecret;
    @Value("${azure.activedirectory.tenant-id}")
    private String tenantId;

    private static final String TOKEN_ENDPOINT =
            "https://login.microsoftonline.com/%s/oauth2/v2.0/token";

    @Autowired
    private EncryptionService encryptionService;

    private final RestTemplate restTemplate = new RestTemplate();

    public List<EmailMessage> getRecentEmails(String accessToken, int minutesAgo) throws Exception {
        String filterDate = OffsetDateTime.now(ZoneOffset.UTC)
                .minusMinutes(minutesAgo)
                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        String url = graphApiBaseUrl + "/me/messages" +
                "?$filter=receivedDateTime ge " + filterDate +
                "&$orderby=receivedDateTime desc" +
                "&$select=id,subject,body,from,receivedDateTime" +
                "&$top=50";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        log.info("Buscando e-mails recentes do Graph API (Filtro: {})...", filterDate);

        try {
            ResponseEntity<GraphEmailResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    GraphEmailResponse.class
            );

            return response.getBody() != null ? response.getBody().getValue() : List.of();
        } catch (Exception e) {
            log.error("Erro ao buscar e-mails do Graph API: {}", e.getMessage());
            return List.of();
        }
    }

    public CompanyTokens refreshAccessToken(String refreshToken) throws Exception {
        log.info("Access token expirado. Renovando...");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("refresh_token", refreshToken);
        params.add("grant_type", "refresh_token");
        params.add("scope", "https://graph.microsoft.com/.default offline_access");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                String.format(TOKEN_ENDPOINT, tenantId),
                request,
                Map.class
        );

        Map<String, Object> responseBody = response.getBody();
        if (responseBody == null || !responseBody.containsKey("access_token")) {
            log.error("Falha ao renovar token: {}", responseBody);
            throw new RuntimeException("Não foi possível renovar o token");
        }

        String newAccessToken = (String) responseBody.get("access_token");
        String newRefreshToken = (String) responseBody.get("refresh_token");
        long expiresIn = ((Number) responseBody.get("expires_in")).longValue();

        CompanyTokens newTokens = new CompanyTokens();
        newTokens.setAccessToken(encryptionService.encrypt(newAccessToken));
        newTokens.setRefreshToken(encryptionService.encrypt(newRefreshToken));
        newTokens.setExpiresAt(LocalDateTime.now().plusSeconds(expiresIn));

        log.info("Tokens renovados com sucesso.");
        return newTokens;
    }

    public void sendEmail(String accessToken, String to, String subject, String content) {
        String url = graphApiBaseUrl + "/me/sendMail";

        Map<String, Object> message = Map.of(
                "message", Map.of(
                        "subject", subject,
                        "body", Map.of(
                                "contentType", "Text",
                                "content", content
                        ),
                        "toRecipients", List.of(
                                Map.of(
                                        "emailAddress", Map.of("address", to)
                                )
                        )
                ),
                "saveToSentItems", true
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(message, headers);

        try {
            restTemplate.postForEntity(url, entity, Void.class);
            log.info("E-mail enviado com sucesso para: {}", to);
        } catch (Exception e) {
            log.error("Erro ao enviar e-mail via Graph API: {}", e.getMessage());
            throw new RuntimeException("Falha ao enviar e-mail: " + e.getMessage());
        }
    }
}