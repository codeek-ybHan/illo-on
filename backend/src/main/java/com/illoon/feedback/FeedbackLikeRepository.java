package com.illoon.feedback;

import com.illoon.feedback.domain.FeedbackLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FeedbackLikeRepository extends JpaRepository<FeedbackLike, FeedbackLike.Pk> {

    Optional<FeedbackLike> findByIdFeedbackIdAndIdUserId(Long feedbackId, Long userId);

    long countByIdFeedbackId(Long feedbackId);

    List<FeedbackLike> findAllByIdFeedbackIdIn(Collection<Long> feedbackIds);
}
