package com.illoon.feedback;

import com.illoon.common.exception.ApiException;
import com.illoon.common.exception.ErrorCode;
import com.illoon.feedback.domain.Feedback;
import com.illoon.feedback.dto.FeedbackResponse;
import com.illoon.user.User;
import com.illoon.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<FeedbackResponse> list() {
        List<Feedback> feedbacks = feedbackRepository.findAllByOrderByCreatedAtAsc();
        if (feedbacks.isEmpty()) return List.of();

        Map<Long, String> authorNames = userRepository.findAllById(
                        feedbacks.stream().map(Feedback::getUserId).collect(Collectors.toSet()))
                .stream().collect(Collectors.toMap(User::getId, User::getName));

        return feedbacks.stream()
                .map(f -> FeedbackResponse.of(f, authorNames.get(f.getUserId())))
                .toList();
    }

    @Transactional
    public FeedbackResponse create(Long userId, String content) {
        Feedback feedback = feedbackRepository.save(
                Feedback.builder().userId(userId).content(content).build());
        String authorName = userRepository.findById(userId).map(User::getName).orElse(null);
        return FeedbackResponse.of(feedback, authorName);
    }

    @Transactional
    public FeedbackResponse setResolved(Long feedbackId, Long requesterId, boolean resolved) {
        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));
        if (!requester.isAdmin()) {
            throw new ApiException(ErrorCode.FORBIDDEN);
        }
        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));
        feedback.resolve(resolved);
        String authorName = userRepository.findById(feedback.getUserId())
                .map(User::getName).orElse(null);
        return FeedbackResponse.of(feedback, authorName);
    }
}
