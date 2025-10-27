package com.challengr.submission.repo;

import com.challengr.submission.model.Submission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubmissionRepo extends JpaRepository<Submission, Long> {
    List<Submission> findByChallengeId(Long challengeId);
}
