package com.challengr.challenge.api;

import com.challengr.challenge.model.Challenge;
import com.challengr.challenge.service.ChallengeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/challenges")
public class ChallengeController {

    private static final Logger log = LoggerFactory.getLogger(ChallengeController.class);
    private final ChallengeService challengeService;

    public ChallengeController(ChallengeService challengeService) {
        this.challengeService = challengeService;
    }

    // 🎯 Récupérer le challenge du jour
    @GetMapping("/today")
    public ResponseEntity<Challenge> getTodayChallenge() {
        log.info("➡️ [GET] Fetching today's challenge");

        try {
            Challenge challenge = challengeService.getTodayChallenge();
            log.info("✅ Challenge retrieved successfully (id={}, title='{}', date={})",
                    challenge.getId(), challenge.getTitle(), challenge.getDate());
            return ResponseEntity.ok(challenge);
        } catch (Exception e) {
            log.error("❌ Failed to fetch today's challenge: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
