package com.mailtasksai.backend.service;

import com.mailtasksai.backend.dto.AITaskResult;
import com.mailtasksai.backend.dto.EmailMessage;
import com.mailtasksai.backend.model.Company;
import com.mailtasksai.backend.model.Task;
import com.mailtasksai.backend.model.TaskStatus;
import com.mailtasksai.backend.model.User;
import com.mailtasksai.backend.repository.CompanyRepository;
import com.mailtasksai.backend.repository.TaskRepository;
import com.mailtasksai.backend.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class EmailListenerService {

    @Autowired private CompanyRepository companyRepository;
    @Autowired private TaskRepository taskRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private GraphApiClient graphApiClient;
    @Autowired private AIProcessingService aiProcessingService;
    @Autowired private AuthService authService; // Injetamos o AuthService

    @Scheduled(fixedDelay = 60000)
    @Transactional // Importante para manter a sessão do banco aberta no loop
    public void pollNewEmails() {
        // CORREÇÃO: Usa o novo método que traz tokens mesmo se expirados
        List<Company> companies = companyRepository.findAllConnectedCompanies();

        for (Company company : companies) {
            try {
                processCompanyEmails(company);
            } catch (Exception e) {
                log.error("Erro ao processar empresa: " + company.getName(), e);
            }
        }
    }

    private void processCompanyEmails(Company company) throws Exception {
        String accessToken = authService.getValidAccessToken(company);

        if (accessToken == null) {
            log.warn("Pular empresa {}: Não foi possível obter token válido.", company.getName());
            return;
        }

        User owner = userRepository.findAll().stream()
                .filter(u -> u.getCompany().getId().equals(company.getId()))
                .findFirst().orElse(null);
        String ownerEmail = (owner != null) ? owner.getEmail() : "";

        List<EmailMessage> emails = graphApiClient.getRecentEmails(accessToken, 5);

        for (EmailMessage email : emails) {
            try {
                String senderEmail = email.getFrom() != null ? email.getFrom().getAddress() : "";

                if (senderEmail.equalsIgnoreCase(ownerEmail)) {
                    continue;
                }

                if (taskRepository.existsByEmailMessageId(email.getId())) continue;

                AITaskResult aiResult = aiProcessingService.processEmail(email);
                if (aiResult == null || aiResult.getResumoTarefa() == null) continue;

                Task task = new Task();
                task.setCompany(company);
                task.setEmailMessageId(email.getId());
                task.setResumoTarefa(aiResult.getResumoTarefa());
                task.setUrgencia(aiResult.getUrgencia());
                task.setCategoriaSugerida(aiResult.getCategoriaSugerida());
                task.setFromEmail(senderEmail);
                task.setReceivedAt(email.getReceivedDateTime());
                task.setStatus(TaskStatus.PENDING);
                task.setAiConfidence(aiResult.getConfidence());
                task.setEmailSubject(email.getSubject());

                // Lógica de limpeza de texto (Mantendo a sua versão corrigida)
                String rawBody = email.getBody() != null ? email.getBody().getContent() : "";
                String cleanBody = rawBody
                        .replaceAll("(?i)<br\\s*/?>", "\n")
                        .replaceAll("(?i)</?p>", "\n\n")
                        .replaceAll("(?i)</?div>", "\n")
                        .replaceAll("(?i)<ul>", "\n")
                        .replaceAll("(?i)</ul>", "\n")
                        .replaceAll("(?i)<li>", "\n• ")
                        .replaceAll("(?i)</li>", "")
                        .replaceAll("<[^>]+>", "")
                        .replaceAll("&nbsp;", " ")
                        .replaceAll("&quot;", "\"")
                        .replaceAll("&amp;", "&")
                        .replaceAll("&lt;", "<")
                        .replaceAll("&gt;", ">")
                        .replaceAll("\\n{3,}", "\n\n")
                        .trim();

                task.setEmailBody(cleanBody);

                taskRepository.save(task);
                log.info("Nova tarefa criada: {}", task.getResumoTarefa());

            } catch (Exception e) {
                log.error("Erro no e-mail ID: {}", email.getId(), e);
            }
        }
    }
}