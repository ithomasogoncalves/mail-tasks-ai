package com.mailtasksai.backend.controller;

import com.mailtasksai.backend.dto.AnalyticsResponse;
import com.mailtasksai.backend.model.User;
import com.mailtasksai.backend.service.AnalyticsService;
import com.mailtasksai.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    @Autowired private UserService userService;

    @GetMapping("/overview")
    public ResponseEntity<AnalyticsResponse> getOverview() {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.findByEmail(email);
        return ResponseEntity.ok(analyticsService.getOverview(user.getCompany().getId()));
    }
}