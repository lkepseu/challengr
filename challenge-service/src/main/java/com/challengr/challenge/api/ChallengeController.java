package com.challengr.challenge.api;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RestController @RequestMapping("/api/challenges")
public class ChallengeController {

    @GetMapping("/today")
    public Map<String,Object> today(){
        return Map.of(
                "id", 1,
                "date", java.time.LocalDate.now().toString(),
                "title", "",
                "prompt", "Create a photo that represents 'contrast' (light vs dark)."
        );
    }

    private static final Logger logger = LoggerFactory.getLogger(ChallengeController.class);

    @GetMapping("/test")
    public String testLogging() {
        logger.info("✅ ChallengeController reached successfully!");
        logger.warn("⚠️ Example warning log");
        logger.error("❌ Example error log");
        return "Logging test OK!";
    }

}
