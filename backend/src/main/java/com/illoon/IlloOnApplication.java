package com.illoon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.TimeZone;

@EnableJpaAuditing
@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
public class IlloOnApplication {

    public static void main(String[] args) {
        // 서버 OS/컨테이너 기본 타임존이 UTC 인 경우가 많아, LocalDateTime.now() 가 전부 어긋나는 것을 방지.
        // (LocalDateTime 은 타임존 정보 없이 직렬화되므로, JVM 기본 타임존을 KST 로 고정해야
        //  프론트에서 new Date(isoString) 로 파싱할 때 실제 한국 시각과 일치한다.)
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
        SpringApplication.run(IlloOnApplication.class, args);
    }
}
