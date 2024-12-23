package com.example.clinic.auth.controller;

import com.example.clinic.auth.dto.UserDTO;
import com.example.clinic.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.prefix}/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody @Validated UserDTO request) {
        String jwt = authService.logIn(request);

        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                .build();
    }

    @PreAuthorize("hasRole('SUPERVISOR')")
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Validated UserDTO request) {
        authService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .build();
    }
}
