package com.illoon.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 도메인 공통 에러 코드. 프론트는 응답 body 의 message 를 그대로 노출한다.
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 공통
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "입력값이 올바르지 않습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "권한이 없습니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "요청한 리소스를 찾을 수 없습니다."),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다."),

    // 인증
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다."),
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),

    // 프로젝트 / 멤버 / 초대
    PROJECT_NOT_FOUND(HttpStatus.NOT_FOUND, "프로젝트를 찾을 수 없습니다."),
    NOT_PROJECT_MEMBER(HttpStatus.FORBIDDEN, "프로젝트 멤버가 아닙니다."),
    NOT_PROJECT_ADMIN(HttpStatus.FORBIDDEN, "프로젝트 관리자만 가능합니다."),
    INVITE_NOT_FOUND(HttpStatus.NOT_FOUND, "초대 링크를 찾을 수 없습니다."),
    INVITE_EXPIRED(HttpStatus.GONE, "만료된 초대 링크입니다."),
    ALREADY_MEMBER(HttpStatus.CONFLICT, "이미 참여 중인 프로젝트입니다."),

    // Task / Sprint / Meeting
    TASK_NOT_FOUND(HttpStatus.NOT_FOUND, "Task를 찾을 수 없습니다."),
    SPRINT_NOT_FOUND(HttpStatus.NOT_FOUND, "Sprint를 찾을 수 없습니다."),
    MEETING_NOT_FOUND(HttpStatus.NOT_FOUND, "회의를 찾을 수 없습니다."),

    // AI
    AI_ANALYZE_FAILED(HttpStatus.BAD_GATEWAY, "AI 분석에 실패했습니다."),
    MEETING_CONTENT_EMPTY(HttpStatus.BAD_REQUEST, "분석할 회의 내용이 없습니다.");

    private final HttpStatus status;
    private final String message;
}
