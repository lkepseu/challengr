package com.challengr.challenge.api;

import org.springframework.web.bind.annotation.*;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/challenges")
public class ChallengeController {

    private static final Logger logger = LoggerFactory.getLogger(ChallengeController.class);

    // ===============================
    // 🎯 Challenge du jour
    // ===============================
    @GetMapping("/today")
    public Map<String, Object> today() {
        logger.info("📅 Requête reçue sur /api/challenges/today");

        try {
            Map<String, Object> challenge = Map.of(
                    "id", 1,
                    "date", java.time.LocalDate.now().toString(),
                    "title", "Daily Photo Challenge",
                    "prompt", "Create a photo that represents 'contrast' (light vs dark)."
            );

            logger.info("✅ Challenge du jour généré avec succès : {}", challenge);
            return challenge;

        } catch (Exception e) {
            logger.error("❌ Erreur lors de la génération du challenge du jour", e);
            return Map.of("error", "Failed to generate daily challenge");
        }
    }

    // ===============================
    // 🧠 Liste des challenges
    // ===============================
    @GetMapping
    public List<Map<String, Object>> listChallenges() {
        logger.info("📜 Récupération de la liste complète des challenges");
        return List.of(
                Map.of("id", 1, "title", "Contrast", "theme", "Photography"),
                Map.of("id", 2, "title", "Symmetry", "theme", "Design")
        );
    }
}

