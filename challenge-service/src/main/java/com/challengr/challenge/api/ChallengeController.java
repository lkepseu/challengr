package com.challengr.challenge.api;

import org.springframework.web.bind.annotation.*;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/challenges")
public class ChallengeController {

    private static final Logger logger = LoggerFactory.getLogger(ChallengeController.class);

    @GetMapping("/today")
    public Map<String, Object> today() {
        logger.info("Received request for today's challenge");

        Map<String, Object> challenge = Map.of(
                "id", 1,
                "date", java.time.LocalDate.now().toString(),
                "title", "Daily Creative Challenge",
                "prompt", "Create a photo that represents 'contrast' (light vs dark)."
        );

        logger.debug("Challenge details: {}", challenge);
        logger.info("Returning today's challenge successfully");

        return challenge;
    }

    @GetMapping("/test")
    public String test() {
        logger.info("✅ ChallengeController - test endpoint called");
        return "ok";
    }


    @GetMapping("/health")
    public Map<String, String> healthCheck() {
        logger.info("Health check endpoint called");
        return Map.of("status", "OK", "service", "challenge-service");
    }
}
