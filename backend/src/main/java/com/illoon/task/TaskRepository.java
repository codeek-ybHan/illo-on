package com.illoon.task;

import com.illoon.task.domain.Task;
import com.illoon.task.domain.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findAllByProjectIdOrderByCreatedAtDesc(Long projectId);

    List<Task> findAllByProjectIdAndSprintIdOrderByCreatedAtDesc(Long projectId, Long sprintId);

    List<Task> findAllByProjectIdAndMeetingIdOrderByCreatedAtDesc(Long projectId, Long meetingId);

    List<Task> findAllByAssigneeId(Long assigneeId);

    List<Task> findAllBySprintId(Long sprintId);

    List<Task> findAllByMeetingId(Long meetingId);

    long countByProjectId(Long projectId);

    long countByMeetingId(Long meetingId);

    long countBySprintId(Long sprintId);

    long countBySprintIdAndStatus(Long sprintId, TaskStatus status);
}
