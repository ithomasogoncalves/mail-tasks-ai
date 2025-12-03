package com.mailtasksai.backend.service;

import com.mailtasksai.backend.dto.AnalyticsResponse;
import com.mailtasksai.backend.model.Category;
import com.mailtasksai.backend.model.TaskStatus;
import com.mailtasksai.backend.model.UrgenciaEnum;
import com.mailtasksai.backend.repository.CategoryRepository;
import com.mailtasksai.backend.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AnalyticsService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    public AnalyticsResponse getOverview(Long companyId) {
        long pending = taskRepository.countByCompanyIdAndStatus(companyId, TaskStatus.PENDING);
        long completed = taskRepository.countByCompanyIdAndStatus(companyId, TaskStatus.COMPLETED);
        long total = pending + completed;

        Map<String, Long> byUrgency = new HashMap<>();
        byUrgency.put("URGENTE", taskRepository.countByCompanyIdAndUrgencia(companyId, UrgenciaEnum.URGENTE));
        byUrgency.put("MEDIANO", taskRepository.countByCompanyIdAndUrgencia(companyId, UrgenciaEnum.MEDIANO));
        byUrgency.put("ROTINEIRA", taskRepository.countByCompanyIdAndUrgencia(companyId, UrgenciaEnum.ROTINEIRA));

        Map<String, Long> byCategory = new HashMap<>();
        List<Category> categories = categoryRepository.findAll();
        for (Category cat : categories) {
            long count = taskRepository.countByCompanyIdAndCategoriaSugerida(companyId, cat.getName());
            if (count > 0) {
                byCategory.put(cat.getName(), count);
            }
        }

        double completionRate = total > 0 ? ((double) completed / total) * 100 : 0.0;
        completionRate = Math.round(completionRate * 10.0) / 10.0;

        return new AnalyticsResponse(
                "30d",
                total,
                completed,
                pending,
                byUrgency,
                byCategory,
                "4.5 hours",
                completionRate
        );
    }
}
