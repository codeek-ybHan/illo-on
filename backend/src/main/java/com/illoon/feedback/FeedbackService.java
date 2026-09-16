package com.illoon.feedback;

import com.illoon.common.exception.ApiException;
import com.illoon.common.exception.ErrorCode;
import com.illoon.feedback.domain.Feedback;
import com.illoon.feedback.domain.FeedbackLike;
import com.illoon.feedback.dto.FeedbackResponse;
import com.illoon.user.User;
import com.illoon.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final FeedbackLikeRepository feedbackLikeRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<FeedbackResponse> list(Long viewerId) {
        List<Feedback> feedbacks = feedbackRepository.findAllByOrderByCreatedAtAsc();
        if (feedbacks.isEmpty()) return List.of();

        Set<Long> feedbackIds = feedbacks.stream().map(Feedback::getId).collect(Collectors.toSet());

        Map<Long, Boolean> authorIsAdminByUserId = userRepository.findAllById(
                        feedbacks.stream().map(Feedback::getUserId).collect(Collectors.toSet()))
                .stream().collect(Collectors.toMap(User::getId, User::isAdmin));

        List<FeedbackLike> likes = feedbackLikeRepository.findAllByIdFeedbackIdIn(feedbackIds);
        Map<Long, Long> likeCounts = likes.stream()
                .collect(Collectors.groupingBy(FeedbackLike::getFeedbackId, Collectors.counting()));
        Set<Long> likedByViewer = likes.stream()
                .filter(l -> l.getUserId().equals(viewerId))
                .map(FeedbackLike::getFeedbackId)
                .collect(Collectors.toSet());

        return feedbacks.stream()
                .map(f -> FeedbackResponse.of(
                        f,
                        Boolean.TRUE.equals(authorIsAdminByUserId.get(f.getUserId())),
                        likeCounts.getOrDefault(f.getId(), 0L),
                        likedByViewer.contains(f.getId())))
                .toList();
    }

    @Transactional
    public FeedbackResponse create(Long userId, String content, Long replyToId) {
        if (replyToId != null && !feedbackRepository.existsById(replyToId)) {
            throw new ApiException(ErrorCode.NOT_FOUND, "답장 대상 피드백을 찾을 수 없습니다.");
        }
        Feedback feedback = feedbackRepository.save(
                Feedback.builder().userId(userId).content(content).replyToId(replyToId).build());
        boolean authorIsAdmin = userRepository.findById(userId).map(User::isAdmin).orElse(false);
        return FeedbackResponse.of(feedback, authorIsAdmin, 0, false);
    }

    @Transactional
    public FeedbackResponse setResolved(Long feedbackId, Long requesterId, boolean resolved) {
        requireAdmin(requesterId);
        Feedback feedback = findFeedback(feedbackId);
        feedback.resolve(resolved);
        boolean authorIsAdmin = userRepository.findById(feedback.getUserId())
                .map(User::isAdmin).orElse(false);
        return toResponse(feedback, requesterId, authorIsAdmin);
    }

    @Transactional
    public FeedbackResponse toggleLike(Long feedbackId, Long userId) {
        Feedback feedback = findFeedback(feedbackId);
        boolean authorIsAdmin = userRepository.findById(feedback.getUserId())
                .map(User::isAdmin).orElse(false);

        var existing = feedbackLikeRepository.findByIdFeedbackIdAndIdUserId(feedbackId, userId);
        if (existing.isPresent()) {
            feedbackLikeRepository.delete(existing.get());
        } else {
            feedbackLikeRepository.save(new FeedbackLike(feedbackId, userId));
        }
        long likeCount = feedbackLikeRepository.countByIdFeedbackId(feedbackId);
        boolean likedByMe = existing.isEmpty();
        return FeedbackResponse.of(feedback, authorIsAdmin, likeCount, likedByMe);
    }

    private FeedbackResponse toResponse(Feedback feedback, Long viewerId, boolean authorIsAdmin) {
        long likeCount = feedbackLikeRepository.countByIdFeedbackId(feedback.getId());
        boolean likedByMe = feedbackLikeRepository
                .findByIdFeedbackIdAndIdUserId(feedback.getId(), viewerId).isPresent();
        return FeedbackResponse.of(feedback, authorIsAdmin, likeCount, likedByMe);
    }

    private User requireAdmin(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));
        if (!user.isAdmin()) {
            throw new ApiException(ErrorCode.FORBIDDEN);
        }
        return user;
    }

    private Feedback findFeedback(Long feedbackId) {
        return feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));
    }
}
