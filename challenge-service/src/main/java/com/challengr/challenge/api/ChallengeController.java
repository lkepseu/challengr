package com.challengr.challenge.api;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController @RequestMapping("/api/challenges")
public class ChallengeController {

    @GetMapping("/today")
    public Map<String,Object> today(){
        return Map.of(
                "id", 1,
                "date", java.time.LocalDate.now().toString(),
                "title", "Daily Creative Prompt",
                "prompt", "Create a photo that represents 'contrast' (light vs dark)."
        );
    }

}
