package com.challengr.submission.repo;
import org.springframework.data.jpa.repository.JpaRepository;
import com.challengr.submission.model.Submission;
import java.util.*;

public interface SubmissionRepo extends JpaRepository<Submission, Long> {
    List<Submission> findTop20ByChallengeIdOrderByCreatedAtDesc(Long challengeId);
}
