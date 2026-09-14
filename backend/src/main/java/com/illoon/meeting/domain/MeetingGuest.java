package com.illoon.meeting.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 프로젝트 멤버가 아닌 외부 참석자. 이름만 기록한다(계정 없음).
 */
@Getter
@Entity
@Table(name = "meeting_guest")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MeetingGuest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meeting_guest_id")
    private Long id;

    @Column(name = "meeting_id", nullable = false)
    private Long meetingId;

    @Column(nullable = false, length = 50)
    private String name;

    @Builder
    private MeetingGuest(Long meetingId, String name) {
        this.meetingId = meetingId;
        this.name = name;
    }
}
