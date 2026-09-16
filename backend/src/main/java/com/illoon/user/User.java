package com.illoon.user;

import com.illoon.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

/**
 * 기획서 §10-1 USER. email 을 로그인 ID 로 사용하고 password 는 BCrypt 해시로 저장한다.
 * ('user' 는 일부 DB 예약어라 테이블명은 users)
 */
@Getter
@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    /** 서비스 전역 관리자 여부. 최초 관리자는 AdminSeedRunner 가 기동 시 지정 이메일로 승격시킨다. */
    @Column(nullable = false)
    @ColumnDefault("false")
    private boolean admin;

    @Builder
    private User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public void updateProfile(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public void updatePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    public void grantAdmin() {
        this.admin = true;
    }
}
