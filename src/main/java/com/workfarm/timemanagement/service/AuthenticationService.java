package com.workfarm.timemanagement.service;

import com.workfarm.timemanagement.dto.auth.LoginRequestDto;
import com.workfarm.timemanagement.dto.auth.LoginResponseDto;
import com.workfarm.timemanagement.dto.auth.RegisterRequestDto;
import com.workfarm.timemanagement.dto.auth.RegisterResponseDto;

public interface AuthenticationService {
    LoginResponseDto login(LoginRequestDto request);

    RegisterResponseDto register(RegisterRequestDto request);
}
