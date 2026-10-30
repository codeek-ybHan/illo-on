package com.illoon.project;
import com.illoon.project.domain.*;
import com.illoon.sprint.SprintRepository;
import com.illoon.task.TaskRepository;
import com.illoon.team.TeamRepository;
import com.illoon.user.UserRepository;
import com.illoon.common.exception.ApiException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class ProjectDeletionTest {
    @Mock ProjectRepository projects;
    @Mock ProjectMemberRepository members;
    @Mock ProjectInviteRepository invites;
    @Mock TeamRepository teams;
    @Mock UserRepository users;
    @Mock SprintRepository sprints;
    @Mock TaskRepository tasks;
    @InjectMocks ProjectService service;
    @Test void deletesTasksAndSprintsBeforeProject() {
        when(projects.existsById(1L)).thenReturn(true);
        when(members.findByIdProjectIdAndIdUserId(1L,7L)).thenReturn(Optional.of(
            ProjectMember.builder().projectId(1L).userId(7L).role(MemberRole.ADMIN).build()));
        service.delete(1L,7L);
        var order = inOrder(tasks,sprints,projects);
        order.verify(tasks).deleteAllByProjectId(1L);
        order.verify(sprints).deleteAllByProjectId(1L);
        order.verify(projects).deleteById(1L);
    }
    @Test void rejectsNonMemberBeforeDeletingAnything() {
        when(projects.existsById(1L)).thenReturn(true);
        assertThrows(ApiException.class, () -> service.delete(1L,7L));
        verifyNoInteractions(tasks,sprints);
        verify(projects,never()).deleteById(any());
    }
}
