package com.illoon.meeting.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 기획서 §10-8 MEETING_MEMBER. 회의 참석자. 복합 PK (meeting_id, user_id).
 */
@Getter
@Entity
@Table(name = "meeting_member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MeetingMember {

    @EmbeddedId
    private Pk id;

    public MeetingMember(Long meetingId, Long userId) {
        this.id = new Pk(meetingId, userId);
    }

    public Long getMeetingId() {
        return id.meetingId;
    }

    public Long getUserId() {
        return id.userId;
    }

    @Getter
    @Embeddable
    @EqualsAndHashCode
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Pk implements Serializable {
        @Column(name = "meeting_id")
        private Long meetingId;

        @Column(name = "user_id")
        private Long userId;

        public Pk(Long meetingId, Long userId) {
            this.meetingId = meetingId;
            this.userId = userId;
        }
    }
}
