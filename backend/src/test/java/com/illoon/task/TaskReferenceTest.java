package com.illoon.task;

import com.illoon.common.exception.ApiException;
import com.illoon.meeting.MeetingRepository;
import com.illoon.project.*;
import com.illoon.sprint.SprintRepository;
import com.illoon.task.domain.Task;
import com.illoon.task.dto.*;
import com.illoon.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskReferenceTest {
    @Mock TaskRepository tasks;
    @Mock ProjectService projects;
    @Mock ProjectRepository projectRepository;
    @Mock ProjectMemberRepository members;
    @Mock UserRepository users;
    @Mock MeetingRepository meetings;
    @Mock SprintRepository sprints;
    @InjectMocks TaskService service;

    @Test void createRejectsForeignOrMissingMeetingBeforeSaving() {
        var req = new TaskCreateRequest(1L, "업무", null, null, null, null, 9L, null);
        assertThrows(ApiException.class, () -> service.create(7L, req));
        verify(meetings).existsByIdAndProjectId(9L, 1L);
        verify(tasks, never()).save(any());
    }

    @Test void createRejectsForeignOrMissingSprintBeforeSaving() {
        var req = new TaskCreateRequest(1L, "업무", null, null, null, null, null, 9L);
        assertThrows(ApiException.class, () -> service.create(7L, req));
        verify(sprints).existsByIdAndProjectId(9L, 1L);
        verify(tasks, never()).save(any());
    }

    @Test void updateRejectsForeignReferenceWithoutMutatingTask() {
        Task task = Task.builder().projectId(1L).title("원래 업무").build();
        when(tasks.findById(5L)).thenReturn(Optional.of(task));
        var req = new TaskUpdateRequest("변경", null, null, null, null, null, 9L, null, null);
        assertThrows(ApiException.class, () -> service.update(5L, 7L, req));
        assertEquals("원래 업무", task.getTitle());
        assertNull(task.getSprintId());
    }

    @Test void sameProjectReferencesAreAccepted() {
        when(meetings.existsByIdAndProjectId(8L, 1L)).thenReturn(true);
        when(sprints.existsByIdAndProjectId(9L, 1L)).thenReturn(true);
        when(tasks.save(any())).thenAnswer(call -> call.getArgument(0));
        service.create(7L, new TaskCreateRequest(1L, "업무", null, null, null, null, 8L, 9L));
        verify(tasks).save(argThat(t -> t.getMeetingId().equals(8L) && t.getSprintId().equals(9L)));
    }
}
