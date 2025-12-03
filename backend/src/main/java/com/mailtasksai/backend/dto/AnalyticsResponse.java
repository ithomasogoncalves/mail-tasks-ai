package com.mailtasksai.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsResponse {
    private String period;
    private long totalTasks;
    private long completedTasks;
    private long pendingTasks;
    private Map<String, Long> tasksByUrgency;
    private Map<String, Long> tasksByCategory;
    private String averageResponseTime;
    private double completionRate;
}