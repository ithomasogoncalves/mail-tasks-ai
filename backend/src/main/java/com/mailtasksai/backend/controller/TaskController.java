package com.mailtasksai.backend.controller;

import com.mailtasksai.backend.dto.TaskRequest;
import com.mailtasksai.backend.model.Task;
import com.mailtasksai.backend.model.UrgenciaEnum;
import com.mailtasksai.backend.model.TaskStatus;
import com.mailtasksai.backend.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/tasks")
@Slf4j
public class TaskController {

    @Autowired
    private TaskService taskService;

    @PostMapping("/send")
    public ResponseEntity<Task> createTask(@RequestBody TaskRequest request, @AuthenticationPrincipal UserDetails userDetails) {
        Long companyId = Long.valueOf(userDetails.getUsername());
        Task task = taskService.createAndSendTask(request, companyId);
        return ResponseEntity.ok(task);
    }

    @GetMapping
    public ResponseEntity<Page<Task>> searchTasks(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) UrgenciaEnum urgencia,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            Pageable pageable) {

        Long companyId = Long.valueOf(userDetails.getUsername());
        Page<Task> tasks = taskService.searchTasks(companyId, query, urgencia, categoria, status, dateFrom, dateTo, pageable);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long taskId) {
        Task task = taskService.getTaskById(taskId);
        return ResponseEntity.ok(task);
    }

    @PatchMapping("/{taskId}/reply")
    public ResponseEntity<Task> sendReply(@PathVariable Long taskId, @RequestBody String message) {
        Task task = taskService.sendReply(taskId, message);
        return ResponseEntity.ok(task);
    }

    @PatchMapping("/{taskId}/complete")
    public ResponseEntity<Task> markAsCompleted(@PathVariable Long taskId) {
        Task task = taskService.markAsCompleted(taskId);
        return ResponseEntity.ok(task);
    }

    @PatchMapping("/{taskId}/viewed")
    public ResponseEntity<Task> markAsViewed(@PathVariable Long taskId) {
        Task task = taskService.markAsViewed(taskId);
        return ResponseEntity.ok(task);
    }
}