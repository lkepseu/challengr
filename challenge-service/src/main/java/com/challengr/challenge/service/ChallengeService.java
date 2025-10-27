package com.challengr.challenge.service;

import com.challengr.challenge.model.Challenge;
import com.challengr.challenge.repository.ChallengeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class ChallengeService {

    private static final Logger log = LoggerFactory.getLogger(ChallengeService.class);
    private final ChallengeRepository repository;

    public ChallengeService(ChallengeRepository repository) {
        this.repository = repository;
    }

    public Challenge getTodayChallenge() {
        LocalDate today = LocalDate.now();
        log.info("[SERVICE] Looking for today's challenge (date={})", today);

        try {
            Optional<Challenge> challengeOpt = repository.findByDate(today);

            if (challengeOpt.isPresent()) {
                Challenge found = challengeOpt.get();
                log.info("✅ Challenge found in database (id={}, title='{}', date={})",
                        found.getId(), found.getTitle(), today);
                return found;
            } else {
                log.warn("⚠️ No challenge found for today (date={}), creating placeholder...", today);
                Challenge fallback = new Challenge(today, "No Challenge Yet", "Stay tuned for upcoming challenge!");
                log.info("ℹ️ Placeholder challenge created (title='{}')", fallback.getTitle());
                return fallback;
            }

        } catch (Exception e) {
            log.error("❌ Error while fetching today's challenge: {}", e.getMessage(), e);
            throw e;
        }
    }
}
