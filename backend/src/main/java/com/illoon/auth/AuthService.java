package com.illoon.auth;

import com.illoon.auth.dto.AuthResponse;
import com.illoon.auth.dto.LoginRequest;
import com.illoon.auth.dto.SignupRequest;
import com.illoon.common.exception.ApiException;
import com.illoon.common.exception.ErrorCode;
import com.illoon.common.security.JwtTokenProvider;
import com.illoon.user.User;
import com.illoon.user.UserRepository;
import com.illoon.user.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    /** 회원가입 — 이메일 중복 확인 후 BCrypt 해시로 저장. (기획서 §11-1: 가입 후 로그인) */
    @Transactional
    public UserResponse signup(SignupRequest req) {
        if (userRepository.existsByEmail(req.email())) {
            throw new ApiException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
        User user = userRepository.save(User.builder()
                .name(req.name())
                .email(req.email())
                .password(passwordEncoder.encode(req.password()))
                .build());
        return UserResponse.from(user);
    }

    /** 로그인 — 이메일/비밀번호 확인 후 JWT 발급. */
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest req) {
        User user = userRepository.findByEmail(req.email())
                .orElseThrow(() -> new ApiException(ErrorCode.LOGIN_FAILED));
        if (!passwordEncoder.matches(req.password(), user.getPassword())) {
            throw new ApiException(ErrorCode.LOGIN_FAILED);
        }
        String token = tokenProvider.createToken(user.getId(), user.getEmail());
        return AuthResponse.of(token, user);
    }
}
