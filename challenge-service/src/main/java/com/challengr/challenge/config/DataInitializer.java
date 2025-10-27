package com.challengr.challenge.config;

import com.challengr.challenge.model.Challenge;
import com.challengr.challenge.repository.ChallengeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(ChallengeRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                repository.saveAll(List.of(
                        new Challenge(LocalDate.now().minusDays(1), "Photo Contrast", "Capture a moment showing strong contrast between light and dark."),
                        new Challenge(LocalDate.now(), "Colors in Motion", "Create something that shows movement through color."),
                        new Challenge(LocalDate.now().plusDays(1), "Hidden Details", "Find beauty in overlooked places.")
                ));
                System.out.println("✅ Static challenges inserted successfully!");
            }
        };
    }
}
