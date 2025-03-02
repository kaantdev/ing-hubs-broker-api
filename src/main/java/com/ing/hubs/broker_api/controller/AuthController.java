package com.ing.hubs.broker_api.controller;

import com.ing.hubs.broker_api.dto.AuthRequestDTO;
import com.ing.hubs.broker_api.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody AuthRequestDTO authRequest) {
        String token = authService.authenticate(authRequest.getUsername(), authRequest.getPassword());
        return ResponseEntity.ok(token);
    }
}
