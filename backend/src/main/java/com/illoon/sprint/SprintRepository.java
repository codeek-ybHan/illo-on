package com.illoon.sprint;

import com.illoon.sprint.domain.Sprint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SprintRepository extends JpaRepository<Sprint, Long> {

    List<Sprint> findAllByProjectIdOrderByStartDateAscCreatedAtAsc(Long projectId);
}
