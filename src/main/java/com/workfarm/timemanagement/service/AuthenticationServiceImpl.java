package com.workfarm.timemanagement.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.workfarm.timemanagement.dto.auth.LoginRequestDto;
import com.workfarm.timemanagement.dto.auth.LoginResponseDto;
import com.workfarm.timemanagement.dto.auth.RegisterRequestDto;
import com.workfarm.timemanagement.dto.auth.RegisterResponseDto;
import com.workfarm.timemanagement.entity.User;
import com.workfarm.timemanagement.entity.UserRole;
import com.workfarm.timemanagement.exception.InvalidCredentialsException;
import com.workfarm.timemanagement.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordVerifier passwordVerifier;
    private final PasswordHasher passwordHasher;
    private final EmailFormatValidator emailFormatValidator;

    @Override
    @Transactional(readOnly = true)
    // Xác thực user bằng email/mật khẩu và trả về thông tin đăng nhập.
    public LoginResponseDto login(LoginRequestDto request) {
        if (!emailFormatValidator.isValid(request.email())) {
            throw new InvalidCredentialsException("Email hoặc mật khẩu không đúng");
        }

        User user = userRepository.findByEmail(request.email())
            .orElseThrow(() -> new InvalidCredentialsException("Email hoặc mật khẩu không đúng"));

        if (!passwordVerifier.matches(request.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Email hoặc mật khẩu không đúng");
        }

        return new LoginResponseDto(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getRole(),
            user.getPomoDuration(),
            user.getBreakDuration(),
            "Đăng nhập thành công"
        );
    }

    @Override
    @Transactional
    // Đăng ký tài khoản mới với role mặc định USER.
    public RegisterResponseDto register(RegisterRequestDto request) {
        if (!emailFormatValidator.isValid(request.email())) {
            throw new IllegalArgumentException("Email không đúng định dạng");
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalStateException("Email đã được sử dụng");
        }

        if (userRepository.existsByUsername(request.username())) {
            throw new IllegalStateException("Tên đăng nhập đã được sử dụng");
        }

        User user = User.builder()
            .username(request.username())
            .email(request.email())
            .passwordHash(passwordHasher.hash(request.password()))
            .role(UserRole.USER)
            .pomoDuration(25)
            .breakDuration(5)
            .build();

        User savedUser = userRepository.save(user);
        return new RegisterResponseDto(
            savedUser.getId(),
            savedUser.getUsername(),
            savedUser.getEmail(),
            savedUser.getRole(),
            savedUser.getPomoDuration(),
            savedUser.getBreakDuration(),
            "Đăng ký thành công"
        );
    }
}
