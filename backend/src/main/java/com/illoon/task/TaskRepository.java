package com.illoon.task;

import com.illoon.task.domain.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findAllByProjectIdOrderByCreatedAtDesc(Long projectId);

    List<Task> findAllByAssigneeId(Long assigneeId);

    List<Task> findAllBySprintId(Long sprintId);

    long countByProjectId(Long projectId);
}
