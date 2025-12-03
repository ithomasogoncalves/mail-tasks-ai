package com.mailtasksai.backend.repository;

import com.mailtasksai.backend.model.CompanyTokens;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface CompanyTokensRepository extends JpaRepository<CompanyTokens, Long> {

    @Transactional
    void deleteByCompanyId(Long companyId);

    Optional<CompanyTokens> findByCompanyId(Long companyId);
}