package com.mailtasksai.backend.controller;

import com.mailtasksai.backend.dto.UserProfileResponse;
import com.mailtasksai.backend.dto.UserProfileUpdateRequest;
import com.mailtasksai.backend.model.User;
import com.mailtasksai.backend.service.AuthService;
import com.mailtasksai.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getProfile() {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return ResponseEntity.ok(userService.getUserProfileByEmail(email));
    }

    @PutMapping("/profile")
    public ResponseEntity<UserProfileResponse> updateProfile(@RequestBody UserProfileUpdateRequest request) {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(userService.updateUserProfileByEmail(email, request));
    }

    @PostMapping("/disconnect-outlook")
    public ResponseEntity<?> disconnectOutlook() {
        String userEmail = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User user = userService.findByEmail(userEmail);

        authService.revokeTokens(user.getCompany().getId());

        return ResponseEntity.ok(Map.of(
                "message", "Outlook desconectado com sucesso",
                "microsoft_connected", false
        ));
    }
}
