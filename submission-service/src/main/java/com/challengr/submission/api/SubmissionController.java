package com.challengr.submission.api;

import com.challengr.submission.model.Submission;
import com.challengr.submission.service.SubmissionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


@RestController
@RequestMapping("/api/submissions")
public class SubmissionController {

    private static final Logger log = LoggerFactory.getLogger(SubmissionController.class);
    private final SubmissionService submissionService;

    public SubmissionController(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    // 📩 Créer une soumission
    @PostMapping
    public ResponseEntity<Submission> createSubmission(
            @RequestBody Submission submission,
            @RequestHeader("Authorization") String authHeader) {

        log.info("➡️ [POST] Creating submission for challengeId={} by user={}",
                submission.getChallengeId(), submission.getUserHandle());

        try {
            Submission saved = submissionService.createSubmission(submission, authHeader);
            log.info("✅ Submission created successfully (id={}, challengeId={}, user={})",
                    saved.getId(), saved.getChallengeId(), saved.getUserHandle());
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            log.error("❌ Failed to create submission (challengeId={}, user={}): {}",
                    submission.getChallengeId(), submission.getUserHandle(), e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // 📋 Récupérer toutes les soumissions
    @GetMapping
    public ResponseEntity<List<Submission>> getAll() {
        log.info("↗️ [GET] Retrieving all submissions");
        List<Submission> all = submissionService.getAll();
        log.info("✅ Retrieved {} submissions", all.size());
        return ResponseEntity.ok(all);
    }

    // 🔎 Récupérer les soumissions d’un challenge spécifique
    @GetMapping("/challenge/{challengeId}")
    public ResponseEntity<List<Submission>> getByChallenge(@PathVariable Long challengeId) {
        log.info("↗️ [GET] Retrieving submissions for challengeId={}", challengeId);
        List<Submission> list = submissionService.getByChallenge(challengeId);
        log.info("✅ Retrieved {} submissions for challengeId={}", list.size(), challengeId);
        return ResponseEntity.ok(list);
    }
}
