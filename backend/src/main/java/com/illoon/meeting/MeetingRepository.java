package com.illoon.meeting;

import com.illoon.meeting.domain.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {

    List<Meeting> findAllByProjectIdOrderByMeetingAtDescCreatedAtDesc(Long projectId);

    List<Meeting> findAllByProjectIdInOrderByMeetingAtDescCreatedAtDesc(Collection<Long> projectIds);
}
