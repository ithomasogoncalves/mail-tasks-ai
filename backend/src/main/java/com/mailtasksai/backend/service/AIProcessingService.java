package com.mailtasksai.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mailtasksai.backend.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class AIProcessingService {

    @Value("${openai.api-key}")
    private String openAIApiKey;

    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AITaskResult processEmail(EmailMessage email) throws Exception {
        log.info("Processando e-mail com IA: {}", email.getSubject());

        String userPrompt = buildPrompt(email);
        String systemPrompt = getSystemPrompt();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openAIApiKey);

        OpenAIChatRequest requestBody = new OpenAIChatRequest(
                "gpt-4o-mini",
                List.of(
                        new OpenAIMessage("system", systemPrompt),
                        new OpenAIMessage("user", userPrompt)
                ),
                0.3,
                Map.of("type", "json_object")
        );

        HttpEntity<OpenAIChatRequest> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<OpenAIResponse> response = restTemplate.postForEntity(
                    OPENAI_API_URL,
                    entity,
                    OpenAIResponse.class
            );

            String jsonResponseContent = response.getBody()
                    .getChoices()
                    .get(0)
                    .getMessage()
                    .getContent();

            log.debug("Resposta JSON da IA: {}", jsonResponseContent);

            return objectMapper.readValue(jsonResponseContent, AITaskResult.class);

        } catch (Exception e) {
            log.error("Erro ao chamar API da OpenAI: {}", e.getMessage());
            throw new RuntimeException("Falha no processamento da IA", e);
        }
    }

    private String getSystemPrompt() {
        return """
            Você é um assistente especializado em análise de e-mails corporativos.
            Sua tarefa é extrair tarefas de e-mails e classificá-las.
            
            SEMPRE retorne um JSON válido no seguinte formato:
            {
              "resumo_tarefa": "string com máximo 300 caracteres, Extraia os dados técnicos da solicitação em formato de lista estruturada (tópicos). Use estilo telegráfico, direto e sem preposições. Identifique obrigatoriamente: Rota, Carga (peso/medidas) (SE A CARGA TIVER MAIS QUE UM VALOR, CALCULE TAMBÉM O TOTAL) e Ação imediata.",
              "urgencia": "URGENTE | MEDIANO | ROTINEIRA",
              "categoria_sugerida": "string (ex: FINANCEIRO, RH, DESENVOLVIMENTO, MARKETING, VENDAS)"
            }
            
            Critérios de urgência:
            - URGENTE: Prazos de até 24h, palavras como "urgente", "imediato", "hoje"
            - MEDIANO: Prazos de 1-3 dias, importância média
            - ROTINEIRA: Sem prazo específico, tarefas administrativas
            
            Seja preciso e direto no resumo.
            
            SEMPRE retorne um JSON válido no seguinte formato:
            {
              "resumo_tarefa": "string...",
              "urgencia": "...",
              "categoria_sugerida": "...",
              "confidence": 0.95
            }
            ...
            """;
    }

    private String buildPrompt(EmailMessage email) {
        String emailBody = email.getBody() != null ? email.getBody().getContent() : "";
        String plainBody = emailBody.replaceAll("<[^>]*>", " ").replaceAll("\\s+", " ").trim();

        if (plainBody.length() > 1500) {
            plainBody = plainBody.substring(0, 1500) + "...";
        }

        return String.format("""
            Analise o seguinte e-mail e extraia a tarefa principal:
            
            Assunto: %s
            De: %s
            Corpo:
            %s
            
            Extraia e retorne a tarefa no formato JSON especificado.
            """,
                email.getSubject(),
                email.getFrom() != null ? email.getFrom().getAddress() : "Desconhecido",
                plainBody
        );
    }
}
