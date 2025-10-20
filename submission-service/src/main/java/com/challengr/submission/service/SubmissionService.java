package com.challengr.submission.service;

import com.challengr.submission.model.Submission;
import com.challengr.submission.repo.SubmissionRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;

@Service
public class SubmissionService {

    private static final Logger log = LoggerFactory.getLogger(SubmissionService.class);

    private final SubmissionRepo submissionRepository;
    private final WebClient.Builder webClientBuilder;

    public SubmissionService(SubmissionRepo submissionRepository,
                             WebClient.Builder webClientBuilder) {
        this.submissionRepository = submissionRepository;
        this.webClientBuilder = webClientBuilder;
    }

    // 🔹 Créer une nouvelle soumission
    public Submission createSubmission(Submission submission, String authHeader) {
        log.info("[SERVICE] Creating submission for user='{}', challengeId={}",
                submission.getUserHandle(), submission.getChallengeId());

        try {
            // 1️⃣ Validation du token via auth-service
            log.debug("Validating token with auth-service...");
            String authResponse = webClientBuilder.build()
                    .get()
                    .uri("http://auth-service:8082/api/auth/validate")
                    .header("Authorization", authHeader)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            log.info("✅ Auth validated successfully: {}", authResponse);

            // 2️⃣ Vérifier l’existence du challenge
            log.debug("Checking challenge existence (challengeId={})...", submission.getChallengeId());
            String challengeResponse = webClientBuilder.build()
                    .get()
                    .uri("http://challenge-service:8080/api/challenges/today")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            log.info("✅ Challenge verified successfully: {}", challengeResponse);

            // 3️⃣ Sauvegarder la soumission en base
            log.debug("💾 Saving submission to database...");
            Submission saved = submissionRepository.save(submission);
            log.info("✅ Submission stored successfully (id={}, user='{}', challengeId={})",
                    saved.getId(), saved.getUserHandle(), saved.getChallengeId());

            return saved;

        } catch (WebClientResponseException e) {
            // Cas où auth-service ou challenge-service renvoie une erreur HTTP
            log.error("❌ External service error (status={}, body={}): {}",
                    e.getRawStatusCode(), e.getResponseBodyAsString(), e.getMessage(), e);
            throw e;

        } catch (Exception e) {
            // Cas générique : timeout, DB, etc.
            log.error("💥 Unexpected error while creating submission for user='{}', challengeId={}: {}",
                    submission.getUserHandle(), submission.getChallengeId(), e.getMessage(), e);
            throw e;
        }
    }

    // 🔹 Récupérer toutes les soumissions
    public List<Submission> getAll() {
        log.info("[SERVICE] Retrieving all submissions...");
        List<Submission> list = submissionRepository.findAll();
        log.info("✅ Retrieved {} submissions from database", list.size());
        return list;
    }

    // 🔹 Récupérer les soumissions d’un challenge
    public List<Submission> getByChallenge(Long challengeId) {
        log.info("[SERVICE] Retrieving submissions for challengeId={}", challengeId);
        List<Submission> list = submissionRepository.findByChallengeId(challengeId);
        log.info("✅ Retrieved {} submissions for challengeId={}", list.size(), challengeId);
        return list;
    }
}
