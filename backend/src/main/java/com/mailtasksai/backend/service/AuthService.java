package com.mailtasksai.backend.service;

import com.mailtasksai.backend.model.Company;
import com.mailtasksai.backend.model.CompanyTokens;
import com.mailtasksai.backend.model.User;
import com.mailtasksai.backend.repository.CompanyRepository;
import com.mailtasksai.backend.repository.CompanyTokensRepository;
import com.mailtasksai.backend.repository.UserRepository;
import com.mailtasksai.backend.util.EncryptionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class AuthService {

    @Value("${azure.activedirectory.client-id}")
    private String clientId;

    @Value("${azure.activedirectory.client-secret}")
    private String clientSecret;

    @Value("${azure.activedirectory.tenant-id}")
    private String tenantId;

    @Value("${azure.activedirectory.redirect-uri}")
    private String redirectUri;

    private static final String AUTHORIZATION_ENDPOINT = "https://login.microsoftonline.com/%s/oauth2/v2.0/authorize";
    private static final String TOKEN_ENDPOINT = "https://login.microsoftonline.com/%s/oauth2/v2.0/token";
    private static final String GRAPH_USER_ENDPOINT = "https://graph.microsoft.com/v1.0/me";

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private CompanyTokensRepository companyTokensRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EncryptionService encryptionService;

    @Autowired
    private GraphApiClient graphApiClient; // Injeção nova para renovação de tokens

    public String getAuthorizationUrl() {
        String state = UUID.randomUUID().toString();
        String scope = "openid profile offline_access https://graph.microsoft.com/Mail.Read https://graph.microsoft.com/Mail.Send https://graph.microsoft.com/User.Read";

        return String.format(AUTHORIZATION_ENDPOINT, tenantId) +
                "?client_id=" + clientId +
                "&response_type=code" +
                "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8) +
                "&response_mode=query" +
                "&scope=" + URLEncoder.encode(scope, StandardCharsets.UTF_8) +
                "&state=" + state +
                "&prompt=consent";
    }

    public CompanyTokens exchangeCodeForTokens(String code, String state) throws Exception {
        RestTemplate restTemplate = new RestTemplate();

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("code", code);
        params.add("redirect_uri", redirectUri);
        params.add("grant_type", "authorization_code");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                String.format(TOKEN_ENDPOINT, tenantId),
                request,
                Map.class
        );

        Map<String, Object> responseBody = response.getBody();
        String accessToken = (String) responseBody.get("access_token");
        String refreshToken = (String) responseBody.get("refresh_token");
        long expiresIn = ((Number) responseBody.get("expires_in")).longValue();

        Map<String, String> userInfo = getUserInfo(accessToken);
        String userName = userInfo.get("displayName");
        String userEmail = userInfo.get("mail") != null ? userInfo.get("mail") : userInfo.get("userPrincipalName");

        User localUser = updateLocalUser(userName, userEmail);

        return saveCompanyTokens(localUser.getCompany(), accessToken, refreshToken, expiresIn);
    }

    public String getValidAccessToken(Company company) {
        CompanyTokens tokens = company.getTokens();
        if (tokens == null) return null;

        if (tokens.getExpiresAt().isBefore(LocalDateTime.now().plusMinutes(5))) {
            try {
                log.info("Token da empresa '{}' expirado (ou quase). Tentando renovar...", company.getName());
                String refreshTokenDecrypted = encryptionService.decrypt(tokens.getRefreshToken());

                CompanyTokens newTokens = graphApiClient.refreshAccessToken(refreshTokenDecrypted);

                tokens.setAccessToken(newTokens.getAccessToken());
                tokens.setRefreshToken(newTokens.getRefreshToken());
                tokens.setExpiresAt(newTokens.getExpiresAt());

                companyTokensRepository.save(tokens);
                log.info("Token renovado e salvo com sucesso para: {}", company.getName());

                return encryptionService.decrypt(newTokens.getAccessToken());
            } catch (Exception e) {
                log.error("Falha crítica ao renovar token automaticamente para {}: {}", company.getName(), e.getMessage());
                return null;
            }
        }
        return encryptionService.decrypt(tokens.getAccessToken());
    }

    private Map<String, String> getUserInfo(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        return (Map<String, String>) restTemplate.exchange(GRAPH_USER_ENDPOINT, HttpMethod.GET, entity, Map.class).getBody();
    }

    private User updateLocalUser(String name, String email) {
        User user = userRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new RuntimeException("Nenhum usuário encontrado para vincular."));

        user.setName(name + " (Conectado)");
        user.setEmail(email);
        return userRepository.save(user);
    }

    private CompanyTokens saveCompanyTokens(Company company, String accessToken, String refreshToken, long expiresInSeconds) {
        CompanyTokens tokens = companyTokensRepository.findByCompanyId(company.getId())
                .orElse(new CompanyTokens());

        tokens.setCompany(company);
        tokens.setAccessToken(encryptionService.encrypt(accessToken));
        tokens.setRefreshToken(encryptionService.encrypt(refreshToken));
        tokens.setExpiresAt(LocalDateTime.now().plusSeconds(expiresInSeconds));

        return companyTokensRepository.save(tokens);
    }

    public void revokeTokens(Long companyId) {
        companyTokensRepository.deleteByCompanyId(companyId);
    }
}