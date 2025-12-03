package com.mailtasksai.backend.repository;

import com.mailtasksai.backend.model.Task;
import com.mailtasksai.backend.model.TaskStatus;
import com.mailtasksai.backend.model.UrgenciaEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {

    boolean existsByEmailMessageId(String emailMessageId);

    List<Task> findByCompanyIdOrderByReceivedAtDesc(Long companyId);

    long countByCompanyIdAndStatus(Long companyId, TaskStatus status);

    long countByCompanyIdAndUrgencia(Long companyId, UrgenciaEnum urgencia);

    long countByCompanyIdAndCategoriaSugerida(Long companyId, String categoriaSugerida);

    Page<Task> findByCompanyId(Long companyId, Pageable pageable);
}