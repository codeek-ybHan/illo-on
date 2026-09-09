package com.illoon.ai;

import com.illoon.ai.domain.MeetingAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MeetingAnalysisRepository extends JpaRepository<MeetingAnalysis, Long> {

    Optional<MeetingAnalysis> findByMeetingId(Long meetingId);

    boolean existsByMeetingId(Long meetingId);
}
