package com.mailtasksai.backend.service;

import com.mailtasksai.backend.dto.UserProfileResponse;
import com.mailtasksai.backend.dto.UserProfileUpdateRequest;
import com.mailtasksai.backend.model.User;
import com.mailtasksai.backend.repository.CompanyRepository;
import com.mailtasksai.backend.repository.CompanyTokensRepository;
import com.mailtasksai.backend.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private CompanyTokensRepository companyTokensRepository;

    public UserProfileResponse getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilizador não encontrado"));
        return toResponse(user);
    }

    public UserProfileResponse getUserProfileByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilizador não encontrado: " + email));
        return toResponse(user);
    }

    public UserProfileResponse updateUserProfileByEmail(String email, UserProfileUpdateRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilizador não encontrado: " + email));

        if (request.getName() != null && !request.getName().isEmpty()) {
            user.setName(request.getName());
        }

        user = userRepository.save(user);
        return toResponse(user);
    }

    private UserProfileResponse toResponse(User user) {
        boolean isConnected = companyTokensRepository.findByCompanyId(user.getCompany().getId()).isPresent();

        return new UserProfileResponse(
                user.getId().toString(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                isConnected,
                user.getCreatedAt(),
                new UserProfileResponse.CompanyInfo(user.getCompany().getId(), user.getCompany().getName())
        );
    }

    public UserProfileResponse updateUserProfile(Long userId, UserProfileUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilizador não encontrado"));
        if (request.getName() != null && !request.getName().isEmpty()) {
            user.setName(request.getName());
        }
        user = userRepository.save(user);
        return toResponse(user);
    }

    public void createTestUserIfNotExists(Long companyId) {
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com e-mail: " + email));
    }
}
