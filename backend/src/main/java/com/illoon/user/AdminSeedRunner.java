package com.illoon.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 기동 시 지정 이메일(app.admin.email) 계정을 서비스 관리자로 승격시킨다.
 * 이 프로젝트엔 마이그레이션/시드 도구가 없어(ddl-auto=update만 존재) 수동 SQL 대신 이 방식으로 처리한다.
 * 아직 그 이메일로 가입된 계정이 없으면 조용히 넘어가고, 다음 기동(가입 이후)에 승격된다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AdminSeedRunner implements ApplicationRunner {

    private final UserRepository userRepository;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        userRepository.findByEmail(adminEmail)
                .filter(u -> !u.isAdmin())
                .ifPresent(u -> {
                    u.grantAdmin();
                    userRepository.save(u);
                    log.info("관리자 계정으로 승격: {}", adminEmail);
                });
    }
}
