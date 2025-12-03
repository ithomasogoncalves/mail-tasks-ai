package com.mailtasksai.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/public")
public class PublicController {

    @PostMapping("/contact")
    public ResponseEntity<?> receiveContact(@RequestBody Map<String, String> formData) {
        System.out.println("NOVO LEAD DA LANDING PAGE:");
        System.out.println("Nome: " + formData.get("name"));
        System.out.println("Email: " + formData.get("email"));
        System.out.println("Empresa: " + formData.get("company"));

        return ResponseEntity.ok(Map.of("message", "Recebido com sucesso"));
    }
}