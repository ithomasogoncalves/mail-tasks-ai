package com.mailtasksai.backend.config;

import com.mailtasksai.backend.model.Category;
import com.mailtasksai.backend.model.Company;
import com.mailtasksai.backend.model.User;
import com.mailtasksai.backend.repository.CategoryRepository;
import com.mailtasksai.backend.repository.CompanyRepository;
import com.mailtasksai.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Value("${ADMIN_PASSWORD:admin123}")
    private String adminPassword;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        if (categoryRepository.count() == 0) {
            categoryRepository.saveAll(Arrays.asList(
                    new Category("FINANCEIRO", "#10b981"),
                    new Category("RH", "#3b82f6"),
                    new Category("DESENVOLVIMENTO", "#8b5cf6"),
                    new Category("MARKETING", "#f59e0b"),
                    new Category("VENDAS", "#ec4899")
            ));
            System.out.println("Categorias inicializadas.");
        }

        Company company;
        if (companyRepository.count() == 0) {
            company = new Company();
            company.setName("Minha Empresa");
            company = companyRepository.save(company);
            System.out.println("Empresa criada com sucesso.");
        } else {
            company = companyRepository.findAll().get(0);
        }

        if (userRepository.count() == 0) {
            User user = new User();
            user.setCompany(company);
            user.setName("Admin Master");
            user.setEmail("ithomasogoncalves@outlook.com");
            user.setRole("admin");
            user.setPassword(new BCryptPasswordEncoder().encode(adminPassword));

            userRepository.save(user);
            System.out.println("### USUÁRIO ADMIN CRIADO ###");
            System.out.println("Login: ithomasogoncalves@outlook.com");
        }
    }
}