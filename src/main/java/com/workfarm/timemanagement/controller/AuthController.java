package com.workfarm.timemanagement.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.workfarm.timemanagement.dto.auth.LoginRequestDto;
import com.workfarm.timemanagement.dto.auth.LoginResponseDto;
import com.workfarm.timemanagement.dto.auth.RegisterRequestDto;
import com.workfarm.timemanagement.dto.auth.RegisterResponseDto;
import com.workfarm.timemanagement.service.AuthenticationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;

    // API đăng nhập.
    @PostMapping("/login")
    public LoginResponseDto login(@Valid @RequestBody LoginRequestDto request) {
        return authenticationService.login(request);
    }

    // API đăng ký.
    @PostMapping("/register")
    public RegisterResponseDto register(@Valid @RequestBody RegisterRequestDto request) {
        return authenticationService.register(request);
    }
}
