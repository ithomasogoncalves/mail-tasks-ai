package com.mailtasksai.backend.controller;

import com.mailtasksai.backend.dto.DashboardResponse;
import com.mailtasksai.backend.dto.TaskRequest;
import com.mailtasksai.backend.dto.TaskStats;
import com.mailtasksai.backend.model.Company;
import com.mailtasksai.backend.model.Task;
import com.mailtasksai.backend.model.User;
import com.mailtasksai.backend.service.TaskService;
import com.mailtasksai.backend.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import com.mailtasksai.backend.model.TaskStatus;
import com.mailtasksai.backend.model.UrgenciaEnum;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Slf4j
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserService userService;

    private Long getCurrentCompanyId() {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.findByEmail(email);
        return user.getCompany().getId();
    }

    @GetMapping("/dashboard/tasks")
    public ResponseEntity<Map<String, Object>> getDashboardTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Long companyId = getCurrentCompanyId();

        Pageable pageable = PageRequest.of(page, size, Sort.by("receivedAt").descending());

        Page<Task> tasksPage = taskService.getTasksByCompany(companyId, pageable);
        TaskStats stats = taskService.getTaskStats(companyId);

        Map<String, Object> response = new HashMap<>();
        response.put("tasks", tasksPage.getContent());
        response.put("stats", stats);

        Map<String, Object> pagination = Map.of(
                "currentPage", tasksPage.getNumber() + 1,
                "totalPages", tasksPage.getTotalPages(),
                "totalItems", tasksPage.getTotalElements(),
                "itemsPerPage", tasksPage.getSize()
        );
        response.put("pagination", pagination);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/tasks/send")
    public ResponseEntity<Map<String, Object>> sendTask(@Valid @RequestBody TaskRequest request) {
        Long companyId = getCurrentCompanyId();

        Task task = taskService.createAndSendTask(request, companyId);

        return ResponseEntity.ok(Map.of(
                "message", "Tarefa enviada com sucesso",
                "task_id", task.getId()
        ));
    }

    @PatchMapping("/tasks/{taskId}/complete")
    public ResponseEntity<?> completeTask(@PathVariable Long taskId) {
        Task updatedTask = taskService.markAsCompleted(taskId);
        return ResponseEntity.ok(Map.of(
                "message", "Tarefa marcada como concluída",
                "task", updatedTask
        ));
    }

    @GetMapping("/tasks/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    @GetMapping("/tasks/search")
    public ResponseEntity<Page<Task>> searchTasks(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) UrgenciaEnum urgencia,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Long companyId = getCurrentCompanyId();

        Pageable pageable = PageRequest.of(page, size, Sort.by("receivedAt").descending());

        Page<Task> tasks = taskService.searchTasks(companyId, q, urgencia, categoria, status, dateFrom, dateTo, pageable);
        return ResponseEntity.ok(tasks);
    }

    @PostMapping("/tasks/{taskId}/notify")
    public ResponseEntity<?> notifyStatus(@PathVariable Long taskId, @RequestParam String status) {
        Task task = taskService.getTaskById(taskId);

        String subject = "Atualização da Tarefa: " + task.getResumoTarefa();
        String body = "Olá,\n\nA tarefa solicitada via e-mail (" + task.getEmailSubject() + ") agora está com status: " + status + ".\n\nAtenciosamente,\nMail Task AI.";

        taskService.sendNotificationEmail(taskId, task.getFromEmail(), subject, body);

        return ResponseEntity.ok(Map.of("message", "Notificação enviada com sucesso"));
    }
}
