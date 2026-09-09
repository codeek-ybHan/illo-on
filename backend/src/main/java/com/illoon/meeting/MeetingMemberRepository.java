package com.illoon.meeting;

import com.illoon.meeting.domain.MeetingMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MeetingMemberRepository extends JpaRepository<MeetingMember, MeetingMember.Pk> {

    List<MeetingMember> findAllByIdMeetingId(Long meetingId);

    long countByIdMeetingId(Long meetingId);

    void deleteAllByIdMeetingId(Long meetingId);
}
