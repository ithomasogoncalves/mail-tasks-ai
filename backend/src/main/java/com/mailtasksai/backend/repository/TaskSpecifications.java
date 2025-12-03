package com.mailtasksai.backend.repository;

import com.mailtasksai.backend.model.Task;
import com.mailtasksai.backend.model.TaskStatus;
import com.mailtasksai.backend.model.UrgenciaEnum;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class TaskSpecifications {

    public static Specification<Task> withCompanyId(Long companyId) {
        return (root, query, cb) -> cb.equal(root.get("company").get("id"), companyId);
    }

    public static Specification<Task> withTextSearch(String text) {
        if (text == null || text.isEmpty()) return null;
        String likePattern = "%" + text.toLowerCase() + "%";
        return (root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("resumoTarefa")), likePattern),
                cb.like(cb.lower(root.get("emailSubject")), likePattern)
        );
    }

    public static Specification<Task> withUrgencia(UrgenciaEnum urgencia) {
        if (urgencia == null) return null;
        return (root, query, cb) -> cb.equal(root.get("urgencia"), urgencia);
    }

    public static Specification<Task> withCategoria(String categoria) {
        if (categoria == null || categoria.isEmpty()) return null;
        return (root, query, cb) -> cb.equal(root.get("categoriaSugerida"), categoria);
    }

    public static Specification<Task> withStatus(TaskStatus status) {
        if (status == null) return null;
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    public static Specification<Task> withDateRange(LocalDate dateFrom, LocalDate dateTo) {
        if (dateFrom == null && dateTo == null) return null;
        return (root, query, cb) -> {
            if (dateFrom != null && dateTo != null) {
                return cb.between(root.get("receivedAt"), dateFrom.atStartOfDay(), dateTo.atTime(23, 59, 59));
            } else if (dateFrom != null) {
                return cb.greaterThanOrEqualTo(root.get("receivedAt"), dateFrom.atStartOfDay());
            } else {
                return cb.lessThanOrEqualTo(root.get("receivedAt"), dateTo.atTime(23, 59, 59));
            }
        };
    }
}
