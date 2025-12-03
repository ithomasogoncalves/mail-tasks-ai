package com.mailtasksai.backend.repository;

import com.mailtasksai.backend.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

    Optional<Company> findByName(String name);

    @Query("SELECT c FROM Company c JOIN FETCH c.tokens t")
    List<Company> findAllConnectedCompanies();
}