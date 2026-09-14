package com.illoon.meeting;

import com.illoon.meeting.domain.MeetingGuest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MeetingGuestRepository extends JpaRepository<MeetingGuest, Long> {

    List<MeetingGuest> findAllByMeetingId(Long meetingId);

    long countByMeetingId(Long meetingId);

    void deleteAllByMeetingId(Long meetingId);
}
