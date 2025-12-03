package com.mailtasksai.backend.service;

import com.mailtasksai.backend.dto.DashboardResponse;
import com.mailtasksai.backend.dto.TaskRequest;
import com.mailtasksai.backend.dto.TaskStats;
import com.mailtasksai.backend.model.*;
import com.mailtasksai.backend.repository.CompanyRepository;
import com.mailtasksai.backend.repository.TaskRepository;
import com.mailtasksai.backend.repository.TaskSpecifications;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private GraphApiClient graphApiClient;

    @Autowired
    private AuthService authService;

    public Page<Task> getTasksByCompany(Long companyId, Pageable pageable) {
        return taskRepository.findByCompanyId(companyId, pageable);
    }

    public DashboardResponse getDashboardData(Long companyId) {
        List<Task> tasks = getRecentTasks(companyId);
        TaskStats stats = getTaskStats(companyId);
        return new DashboardResponse(tasks, stats);
    }

    public List<Task> getRecentTasks(Long companyId) {
        return taskRepository.findByCompanyIdOrderByReceivedAtDesc(companyId);
    }

    public TaskStats getTaskStats(Long companyId) {
        long urgentCount = taskRepository.countByCompanyIdAndUrgencia(companyId, UrgenciaEnum.URGENTE);
        long pendingCount = taskRepository.countByCompanyIdAndStatus(companyId, TaskStatus.PENDING);
        long completedCount = taskRepository.countByCompanyIdAndStatus(companyId, TaskStatus.COMPLETED);

        return new TaskStats(urgentCount, pendingCount, completedCount);
    }

    public Task createAndSendTask(TaskRequest request, Long companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));

        Task task = new Task();
        task.setCompany(company);
        task.setResumoTarefa(request.getTitle());
        task.setUrgencia(request.getUrgencia());
        task.setCategoriaSugerida(request.getCategory());
        task.setToEmail(request.getRecipient());
        task.setStatus(TaskStatus.PENDING);
        task.setReceivedAt(LocalDateTime.now());

        task.setEmailMessageId("manual-" + UUID.randomUUID().toString());
        task.setFromEmail("painel@mailtasks.ai");

        String emailSubject = "Nova Tarefa: " + request.getTitle();
        String emailBody = String.format(
                "Uma nova tarefa foi atribuída a você via MailTasks AI.\n\n" +
                        "Título: %s\n" +
                        "Categoria: %s\n" +
                        "Urgência: %s\n\n" +
                        "Por favor, verifique o painel para mais detalhes.",
                request.getTitle(), request.getCategory(), request.getUrgencia()
        );

        task.setEmailSubject(emailSubject);
        task.setEmailBody(emailBody);

        try {
            String accessToken = authService.getValidAccessToken(company);

            if (accessToken != null) {
                log.info("Tokens válidos encontrados. Enviando e-mail...");
                graphApiClient.sendEmail(accessToken, request.getRecipient(), emailSubject, emailBody);
                log.info("E-mail enviado via Outlook para {}", request.getRecipient());
            } else {
                log.warn("Empresa sem conexão válida com Outlook. A tarefa foi criada, mas o e-mail NÃO foi enviado.");
            }
        } catch (Exception e) {
            log.error("Erro crítico ao tentar enviar e-mail: " + e.getMessage());
        }

        return taskRepository.save(task);
    }

    public Task markAsCompleted(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Tarefa não encontrada com ID: " + taskId));

        task.setStatus(TaskStatus.COMPLETED);
        log.info("Marcando tarefa ID: {} como concluída.", taskId);
        return taskRepository.save(task);
    }

    public Task getTaskById(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Tarefa não encontrada com ID: " + taskId));
    }

    public Page<Task> searchTasks(Long companyId, String query, UrgenciaEnum urgencia,
                                  String categoria, TaskStatus status,
                                  LocalDate dateFrom, LocalDate dateTo, Pageable pageable) {

        Specification<Task> spec = TaskSpecifications.withCompanyId(companyId)
                .and(TaskSpecifications.withTextSearch(query))
                .and(TaskSpecifications.withUrgencia(urgencia))
                .and(TaskSpecifications.withCategoria(categoria))
                .and(TaskSpecifications.withStatus(status))
                .and(TaskSpecifications.withDateRange(dateFrom, dateTo));

        return taskRepository.findAll(spec, pageable);
    }

    public void sendNotificationEmail(Long taskId, String to, String subject, String body) {
        Task task = getTaskById(taskId);
        Company company = task.getCompany();

        try {
            String accessToken = authService.getValidAccessToken(company);

            if (accessToken != null) {
                graphApiClient.sendEmail(accessToken, to, subject, body);
                log.info("Notificação enviada com sucesso para: {}", to);
            } else {
                log.warn("Falha ao obter token válido. Notificação não enviada.");
                throw new RuntimeException("Token inválido ou expirado.");
            }
        } catch (Exception e) {
            log.error("Erro ao enviar notificação: {}", e.getMessage());
            throw new RuntimeException("Falha ao enviar e-mail: " + e.getMessage());
        }
    }
}