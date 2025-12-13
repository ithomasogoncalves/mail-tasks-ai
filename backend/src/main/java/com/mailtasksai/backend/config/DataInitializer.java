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

import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

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
        }

        Company company;
        if (companyRepository.count() == 0) {
            company = new Company();
            company.setName("Minha Empresa Demo");
            company = companyRepository.save(company);
        } else {
            company = companyRepository.findAll().get(0);
        }

        String emailDemo = "ithomasogoncalves@outlook.com";
        String senhaDemo = "Thomas16632";

        List<User> users = userRepository.findByEmail(emailDemo);
        User user;

        if (users.isEmpty()) {
            user = new User();
        } else {
            user = users.get(0);
            if (users.size() > 1) {
                for (int i = 1; i < users.size(); i++) {
                    userRepository.delete(users.get(i));
                }
            }
        }

        user.setCompany(company);
        user.setName("Thomás Gonçalves");
        user.setEmail(emailDemo);
        user.setRole("admin");
        user.setPassword(new BCryptPasswordEncoder().encode(senhaDemo));

        userRepository.save(user);

        System.out.println("### USUÁRIO DE DEMONSTRAÇÃO ATUALIZADO ###");
        System.out.println("Login: " + emailDemo);
        System.out.println("Senha: " + senhaDemo);
    }
}