package com.mailtasksai.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
@Data
@NoArgsConstructor
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Company company;

    @Column(name = "email_message_id", nullable = false)
    private String emailMessageId;

    @Column(name = "resumo_tarefa", nullable = false, columnDefinition = "TEXT")
    private String resumoTarefa;

    @Column(name = "email_subject", columnDefinition = "TEXT")
    private String emailSubject;

    @Column(name = "email_body", columnDefinition = "TEXT")
    private String emailBody;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UrgenciaEnum urgencia;

    @Column(name = "categoria_sugerida", nullable = false)
    private String categoriaSugerida;

    @Column(name = "from_email", nullable = false)
    private String fromEmail;

    @Column(name = "to_email")
    private String toEmail;

    @Column(name = "received_at", nullable = false)
    private LocalDateTime receivedAt;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(name = "ai_confidence")
    private Double aiConfidence;

    @Column(name = "completion_message", columnDefinition = "TEXT")
    private String completionMessage;

    @Column(name = "ai_summary_formatted", columnDefinition = "TEXT")
    private String aiSummaryFormatted;
}