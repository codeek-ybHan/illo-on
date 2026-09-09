package com.illoon.project;

import com.illoon.common.exception.ApiException;
import com.illoon.common.exception.ErrorCode;
import com.illoon.project.domain.*;
import com.illoon.project.dto.*;
import com.illoon.project.repository.ProjectInviteRepository;
import com.illoon.project.repository.ProjectMemberRepository;
import com.illoon.project.repository.ProjectRepository;
import com.illoon.team.Team;
import com.illoon.team.TeamRepository;
import com.illoon.user.User;
import com.illoon.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private static final int INVITE_VALID_DAYS = 7;

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository memberRepository;
    private final ProjectInviteRepository inviteRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;

    @Value("${app.invite.base-url}")
    private String inviteBaseUrl;

    // ---------- Project CRUD ----------

    @Transactional(readOnly = true)
    public List<ProjectResponse> listMyProjects(Long userId) {
        List<Project> projects = projectRepository.findAllByMember(userId);
        return projects.stream()
                .map(p -> ProjectResponse.of(
                        p,
                        roleOf(p.getId(), userId),
                        memberRepository.countByIdProjectId(p.getId())))
                .toList();
    }

    @Transactional
    public ProjectResponse create(Long userId, ProjectCreateRequest req) {
        Team team = teamRepository.save(Team.builder().name(req.name()).build());
        Project project = projectRepository.save(Project.builder()
                .teamId(team.getId())
                .name(req.name())
                .description(req.description())
                .startDate(req.startDate())
                .endDate(req.endDate())
                .status(ProjectStatus.PLANNED)
                .build());
        memberRepository.save(ProjectMember.builder()
                .projectId(project.getId())
                .userId(userId)
                .role(MemberRole.ADMIN)
                .build());
        return ProjectResponse.of(project, MemberRole.ADMIN, 1);
    }

    @Transactional(readOnly = true)
    public ProjectResponse get(Long projectId, Long userId) {
        ProjectMember me = requireMember(projectId, userId);
        Project project = findProject(projectId);
        return ProjectResponse.of(project, me.getRole(),
                memberRepository.countByIdProjectId(projectId));
    }

    @Transactional
    public ProjectResponse update(Long projectId, Long userId, ProjectUpdateRequest req) {
        requireAdmin(projectId, userId);
        Project project = findProject(projectId);
        project.update(req.name(), req.description(), req.startDate(), req.endDate(), req.status());
        return ProjectResponse.of(project, MemberRole.ADMIN,
                memberRepository.countByIdProjectId(projectId));
    }

    @Transactional
    public void delete(Long projectId, Long userId) {
        requireAdmin(projectId, userId);
        memberRepository.deleteAll(memberRepository.findAllByIdProjectId(projectId));
        projectRepository.deleteById(projectId);
    }

    // ---------- Members ----------

    @Transactional(readOnly = true)
    public List<MemberResponse> listMembers(Long projectId, Long userId) {
        requireMember(projectId, userId);
        List<ProjectMember> members = memberRepository.findAllByIdProjectId(projectId);
        Map<Long, User> users = userRepository.findAllById(
                        members.stream().map(ProjectMember::getUserId).toList())
                .stream().collect(Collectors.toMap(User::getId, Function.identity()));
        return members.stream()
                .map(m -> MemberResponse.of(users.get(m.getUserId()), m.getRole()))
                .sorted(Comparator.comparing(MemberResponse::role)) // ADMIN 먼저
                .toList();
    }

    // ---------- Invite ----------

    @Transactional
    public InviteResponse createInvite(Long projectId, Long userId) {
        requireAdmin(projectId, userId);
        ProjectInvite invite = inviteRepository.save(ProjectInvite.builder()
                .projectId(projectId)
                .token(UUID.randomUUID().toString().replace("-", ""))
                .createdBy(userId)
                .expiresAt(LocalDateTime.now().plusDays(INVITE_VALID_DAYS))
                .build());
        return InviteResponse.of(invite, inviteBaseUrl + invite.getToken());
    }

    @Transactional
    public ProjectResponse joinByInvite(String token, Long userId) {
        ProjectInvite invite = inviteRepository.findByToken(token)
                .orElseThrow(() -> new ApiException(ErrorCode.INVITE_NOT_FOUND));
        if (invite.isExpired()) {
            throw new ApiException(ErrorCode.INVITE_EXPIRED);
        }
        Long projectId = invite.getProjectId();
        if (memberRepository.existsByIdProjectIdAndIdUserId(projectId, userId)) {
            throw new ApiException(ErrorCode.ALREADY_MEMBER);
        }
        memberRepository.save(ProjectMember.builder()
                .projectId(projectId)
                .userId(userId)
                .role(MemberRole.MEMBER)
                .build());
        Project project = findProject(projectId);
        return ProjectResponse.of(project, MemberRole.MEMBER,
                memberRepository.countByIdProjectId(projectId));
    }

    // ---------- guards ----------

    public ProjectMember requireMember(Long projectId, Long userId) {
        if (!projectRepository.existsById(projectId)) {
            throw new ApiException(ErrorCode.PROJECT_NOT_FOUND);
        }
        return memberRepository.findByIdProjectIdAndIdUserId(projectId, userId)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_PROJECT_MEMBER));
    }

    public void requireAdmin(Long projectId, Long userId) {
        if (!requireMember(projectId, userId).isAdmin()) {
            throw new ApiException(ErrorCode.NOT_PROJECT_ADMIN);
        }
    }

    private Project findProject(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new ApiException(ErrorCode.PROJECT_NOT_FOUND));
    }

    private MemberRole roleOf(Long projectId, Long userId) {
        return memberRepository.findByIdProjectIdAndIdUserId(projectId, userId)
                .map(ProjectMember::getRole)
                .orElse(null);
    }
}
