package com.illoon.meeting;

import com.illoon.ai.AiService;
import com.illoon.common.exception.ApiException;
import com.illoon.common.exception.ErrorCode;
import com.illoon.meeting.domain.Meeting;
import com.illoon.meeting.domain.MeetingGuest;
import com.illoon.meeting.domain.MeetingMember;
import com.illoon.meeting.dto.MeetingCreateRequest;
import com.illoon.meeting.dto.MeetingDetailResponse;
import com.illoon.meeting.dto.MeetingResponse;
import com.illoon.meeting.dto.MeetingUpdateRequest;
import com.illoon.project.ProjectService;
import com.illoon.project.domain.Project;
import com.illoon.project.ProjectMemberRepository;
import com.illoon.project.ProjectRepository;
import com.illoon.task.TaskRepository;
import com.illoon.user.User;
import com.illoon.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MeetingService {

    private final MeetingRepository meetingRepository;
    private final MeetingMemberRepository meetingMemberRepository;
    private final MeetingGuestRepository meetingGuestRepository;
    private final ProjectService projectService;
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final AiService aiService;

    /** 외부 참석자는 한 회의당 이만큼만 (오남용 방지). */
    private static final int MAX_GUESTS = 20;

    /** 내가 속한 모든 프로젝트의 회의 (메인보드 · 캘린더용). from/to 는 meetingAt 기준 날짜 필터. */
    @Transactional(readOnly = true)
    public List<MeetingResponse> listMine(Long userId, LocalDate from, LocalDate to) {
        List<Project> projects = projectRepository.findAllByMember(userId);
        if (projects.isEmpty()) return List.of();
        Map<Long, String> names = projects.stream()
                .collect(Collectors.toMap(Project::getId, Project::getName));

        return meetingRepository.findAllByProjectIdInOrderByMeetingAtDescCreatedAtDesc(names.keySet())
                .stream()
                .filter(m -> from == null || m.getMeetingAt() == null
                        || !m.getMeetingAt().toLocalDate().isBefore(from))
                .filter(m -> to == null || m.getMeetingAt() == null
                        || !m.getMeetingAt().toLocalDate().isAfter(to))
                .map(m -> MeetingResponse.of(m, names.get(m.getProjectId()),
                        attendeeCount(m.getId()),
                        aiService.hasAnalysis(m.getId()),
                        taskRepository.countByMeetingId(m.getId())))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MeetingResponse> listByProject(Long projectId, Long userId) {
        projectService.requireMember(projectId, userId);
        String projectName = projectName(projectId);
        return meetingRepository.findAllByProjectIdOrderByMeetingAtDescCreatedAtDesc(projectId)
                .stream()
                .map(m -> MeetingResponse.of(m, projectName,
                        attendeeCount(m.getId()),
                        aiService.hasAnalysis(m.getId()),
                        taskRepository.countByMeetingId(m.getId())))
                .toList();
    }

    @Transactional(readOnly = true)
    public MeetingDetailResponse get(Long meetingId, Long userId) {
        Meeting meeting = findMeeting(meetingId);
        projectService.requireMember(meeting.getProjectId(), userId);
        return toDetail(meeting);
    }

    @Transactional
    public MeetingDetailResponse create(Long projectId, Long userId, MeetingCreateRequest req) {
        projectService.requireMember(projectId, userId);
        validateAttendees(projectId, req.attendeeIds());

        Meeting meeting = meetingRepository.save(Meeting.builder()
                .projectId(projectId)
                .title(req.title())
                .content(req.content())
                .meetingAt(req.meetingAt())
                .createdBy(userId)
                .build());
        saveAttendees(meeting.getId(), req.attendeeIds());
        saveGuests(meeting.getId(), req.guestNames());
        return toDetail(meeting);
    }

    @Transactional
    public MeetingDetailResponse update(Long meetingId, Long userId, MeetingUpdateRequest req) {
        Meeting meeting = findMeeting(meetingId);
        projectService.requireMember(meeting.getProjectId(), userId);
        validateAttendees(meeting.getProjectId(), req.attendeeIds());

        meeting.update(req.title(), req.content(), req.meetingAt());
        if (req.attendeeIds() != null) {
            meetingMemberRepository.deleteAllByIdMeetingId(meetingId);
            saveAttendees(meetingId, req.attendeeIds());
        }
        if (req.guestNames() != null) {
            meetingGuestRepository.deleteAllByMeetingId(meetingId);
            saveGuests(meetingId, req.guestNames());
        }
        return toDetail(meeting);
    }

    @Transactional
    public void delete(Long meetingId, Long userId) {
        Meeting meeting = findMeeting(meetingId);
        projectService.requireMember(meeting.getProjectId(), userId);
        meetingMemberRepository.deleteAllByIdMeetingId(meetingId);
        meetingGuestRepository.deleteAllByMeetingId(meetingId);
        // 회의에서 생성된 Task 는 유지하고 meeting_id 만 끊는다
        taskRepository.findAllByMeetingId(meetingId).forEach(t -> t.update(
                t.getTitle(), t.getDescription(), t.getAssigneeId(), t.getDueDate(),
                t.getPriority(), t.getStatus(), t.getSprintId(), null, t.getCompletedAt()));
        meetingRepository.delete(meeting);
    }

    // ---------- helpers ----------

    private void validateAttendees(Long projectId, List<Long> attendeeIds) {
        if (attendeeIds == null) return;
        for (Long uid : attendeeIds) {
            if (!projectMemberRepository.existsByIdProjectIdAndIdUserId(projectId, uid)) {
                throw new ApiException(ErrorCode.INVALID_INPUT, "참석자는 프로젝트 멤버여야 합니다.");
            }
        }
    }

    private void saveAttendees(Long meetingId, List<Long> attendeeIds) {
        if (attendeeIds == null) return;
        attendeeIds.stream().distinct()
                .forEach(uid -> meetingMemberRepository.save(new MeetingMember(meetingId, uid)));
    }

    /** 공백 제거·중복 제거·개수 제한 후 저장. 잘못된 항목은 조용히 걸러낸다(자유 입력이라 관대하게). */
    private void saveGuests(Long meetingId, List<String> guestNames) {
        if (guestNames == null) return;
        guestNames.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(n -> !n.isBlank())
                .distinct()
                .limit(MAX_GUESTS)
                .forEach(name -> meetingGuestRepository.save(
                        MeetingGuest.builder().meetingId(meetingId).name(name).build()));
    }

    private long attendeeCount(Long meetingId) {
        return meetingMemberRepository.countByIdMeetingId(meetingId)
                + meetingGuestRepository.countByMeetingId(meetingId);
    }

    private MeetingDetailResponse toDetail(Meeting meeting) {
        List<Long> userIds = meetingMemberRepository.findAllByIdMeetingId(meeting.getId())
                .stream().map(MeetingMember::getUserId).toList();
        Map<Long, User> users = userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));
        List<MeetingDetailResponse.Attendee> attendees = userIds.stream()
                .map(users::get).filter(Objects::nonNull)
                .map(u -> new MeetingDetailResponse.Attendee(u.getId(), u.getName(), u.getEmail()))
                .toList();
        List<String> guestNames = meetingGuestRepository.findAllByMeetingId(meeting.getId())
                .stream().map(MeetingGuest::getName).toList();
        return MeetingDetailResponse.of(meeting, projectName(meeting.getProjectId()),
                attendees, guestNames, aiService.hasAnalysis(meeting.getId()),
                taskRepository.countByMeetingId(meeting.getId()));
    }

    private String projectName(Long projectId) {
        return projectRepository.findById(projectId).map(Project::getName).orElse(null);
    }

    private Meeting findMeeting(Long meetingId) {
        return meetingRepository.findById(meetingId)
                .orElseThrow(() -> new ApiException(ErrorCode.MEETING_NOT_FOUND));
    }
}
