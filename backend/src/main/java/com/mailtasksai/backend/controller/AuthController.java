package com.mailtasksai.backend.controller;

import com.mailtasksai.backend.model.User;
import com.mailtasksai.backend.repository.UserRepository;
import com.mailtasksai.backend.service.AuthService;
import com.mailtasksai.backend.service.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List; // Importação adicionada
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @org.springframework.beans.factory.annotation.Value("${app.frontend-url}")
    private String frontendUrl;

    public record LoginRequest(String email, String password) {}

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        System.out.println("--- TENTATIVA DE LOGIN ---");
        System.out.println("Email recebido: " + request.email());
        System.out.println("Senha recebida: " + request.password());

        List<User> users = userRepository.findByEmail(request.email());
        User user = users.isEmpty() ? null : users.get(0); // Pega o primeiro se existir

        if (user == null) {
            System.out.println("ERRO: Usuário não encontrado no banco de dados.");
            return ResponseEntity.status(401).body(Map.of("error", "Usuário não encontrado"));
        }

        System.out.println("Usuário encontrado: " + user.getName());
        System.out.println("Senha no banco (Hash): " + user.getPassword());

        boolean senhaValida = passwordEncoder.matches(request.password(), user.getPassword());
        System.out.println("Senha confere? " + senhaValida);

        if (!senhaValida) {
            System.out.println("ERRO: Senha incorreta.");
            return ResponseEntity.status(401).body(Map.of("error", "Senha incorreta"));
        }

        String token = jwtService.generateToken(String.valueOf(user.getCompany().getId()));
        System.out.println("SUCESSO: Token gerado com Company ID: " + user.getCompany().getId());

        return ResponseEntity.ok(Map.of(
                "token", token,
                "name", user.getName()
        ));
    }

    @GetMapping("/authorize")
    public ResponseEntity<Map<String, String>> getAuthorizationUrl() {
        String url = authService.getAuthorizationUrl();
        return ResponseEntity.ok(Map.of("authorizationUrl", url));
    }

    @GetMapping("/callback")
    public void callback(@RequestParam String code, @RequestParam String state, HttpServletResponse response) throws IOException {
        try {
            log.info("Processando login via Microsoft...");
            var tokens = authService.exchangeCodeForTokens(code, state);
            response.sendRedirect(frontendUrl + "/settings?status=success");
        } catch (Exception e) {
            log.error("Erro no login", e);
            response.sendRedirect(frontendUrl + "/settings?status=error");
        }
    }

    @PostMapping("/revoke/{companyId}")
    public ResponseEntity<?> revokeAccess(@PathVariable Long companyId) {
        authService.revokeTokens(companyId);
        return ResponseEntity.ok(Map.of("message", "Acesso revogado com sucesso"));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok(Map.of("message", "Desconectado com sucesso"));
    }
}