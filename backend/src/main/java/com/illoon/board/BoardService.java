package com.illoon.board;

import com.illoon.board.dto.BoardResponse;
import com.illoon.board.dto.BoardResponse.BoardProject;
import com.illoon.board.dto.BoardResponse.BoardSprint;
import com.illoon.meeting.MeetingService;
import com.illoon.meeting.dto.MeetingResponse;
import com.illoon.project.domain.Project;
import com.illoon.project.ProjectMemberRepository;
import com.illoon.project.ProjectRepository;
import com.illoon.sprint.SprintRepository;
import com.illoon.sprint.domain.SprintStatus;
import com.illoon.task.TaskRepository;
import com.illoon.task.TaskService;
import com.illoon.task.domain.TaskStatus;
import com.illoon.task.dto.TaskResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final SprintRepository sprintRepository;
    private final TaskRepository taskRepository;
    private final TaskService taskService;
    private final MeetingService meetingService;

    @Transactional(readOnly = true)
    public BoardResponse getBoard(Long userId) {
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        List<Project> myProjects = projectRepository.findAllByMember(userId);

        List<BoardProject> projects = myProjects.stream().map(p -> {
            long total = taskRepository.countByProjectId(p.getId());
            long done = taskRepository.countByProjectIdAndStatus(p.getId(), TaskStatus.DONE);
            return new BoardProject(p.getId(), p.getName(), p.getStatus().name(),
                    pct(done, total), total, projectMemberRepository.countByIdProjectId(p.getId()));
        }).toList();

        List<BoardSprint> activeSprints = new ArrayList<>();
        for (Project p : myProjects) {
            sprintRepository.findAllByProjectIdOrderByStartDateAscCreatedAtAsc(p.getId()).stream()
                    .filter(s -> s.getStatus() == SprintStatus.ACTIVE)
                    .forEach(s -> {
                        long t = taskRepository.countBySprintId(s.getId());
                        long d = taskRepository.countBySprintIdAndStatus(s.getId(), TaskStatus.DONE);
                        activeSprints.add(new BoardSprint(s.getId(), p.getId(), p.getName(),
                                s.getName(), s.getEndDate(), pct(d, t), t, d));
                    });
        }

        List<TaskResponse> myTasks = taskService.listMine(userId);
        List<TaskResponse> open = myTasks.stream()
                .filter(t -> t.status() != TaskStatus.DONE).toList();

        int dueSoon = (int) open.stream().filter(t -> withinDays(t.dueDate(), today, 3)).count();
        int overdue = (int) open.stream()
                .filter(t -> t.dueDate() != null && t.dueDate().toLocalDate().isBefore(today)).count();
        List<TaskResponse> todayTasks = open.stream()
                .filter(t -> t.dueDate() != null && t.dueDate().toLocalDate().isEqual(today)).toList();

        List<MeetingResponse> allMeetings = meetingService.listMine(userId, null, null);
        List<MeetingResponse> todayMeetings = allMeetings.stream()
                .filter(m -> m.meetingAt() != null && m.meetingAt().toLocalDate().isEqual(today)).toList();
        int upcoming = (int) allMeetings.stream()
                .filter(m -> m.meetingAt() != null && m.meetingAt().isAfter(now)).count();

        return new BoardResponse(
                open.size(), dueSoon, overdue, todayTasks.size(), upcoming,
                projects, activeSprints, todayMeetings, todayTasks);
    }

    private int pct(long done, long total) {
        return total == 0 ? 0 : (int) Math.round(done * 100.0 / total);
    }

    private boolean withinDays(LocalDateTime dt, LocalDate today, int days) {
        if (dt == null) return false;
        LocalDate d = dt.toLocalDate();
        return !d.isBefore(today) && !d.isAfter(today.plusDays(days));
    }
}
