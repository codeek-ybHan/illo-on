package com.illoon.user;

import com.illoon.common.exception.ApiException;
import com.illoon.common.exception.ErrorCode;
import com.illoon.user.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse updateProfile(Long userId, String name, String email) {
        User user = findUser(userId);
        if (!user.getEmail().equals(email) && userRepository.existsByEmail(email)) {
            throw new ApiException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
        user.updateProfile(name, email);
        return UserResponse.from(user);
    }

    @Transactional
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        User user = findUser(userId);
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new ApiException(ErrorCode.PASSWORD_MISMATCH);
        }
        user.updatePassword(passwordEncoder.encode(newPassword));
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));
    }
}
