package com.challengr.submission.api;
import org.springframework.web.bind.annotation.*; import org.springframework.http.*;
import com.challengr.submission.repo.SubmissionRepo; import com.challengr.submission.model.Submission;
import java.util.*;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import net.coobird.thumbnailator.Thumbnails;
import java.io.InputStream;
import java.io.OutputStream;


@RestController @RequestMapping("/api/submissions")
public class SubmissionController {
    private final SubmissionRepo repo;
    public SubmissionController(SubmissionRepo repo){ this.repo = repo; }

    @GetMapping
    public List<Submission> list(@RequestParam Long challengeId){
        return repo.findTop20ByChallengeIdOrderByCreatedAtDesc(challengeId);
    }

    @PostMapping
    public ResponseEntity<Submission> create(@RequestBody Submission s){
        if(s.getChallengeId()==null) return ResponseEntity.badRequest().build();
        if(s.getContentType()==null) s.setContentType("text");
        if("text".equals(s.getContentType()) && (s.getContent()==null || s.getContent().isBlank()))
            return ResponseEntity.badRequest().body(null);
        if("image".equals(s.getContentType()) && (s.getImageUrl()==null || s.getImageUrl().isBlank()))
            return ResponseEntity.badRequest().body(null);
        if(s.getUserHandle()==null) s.setUserHandle("guest");
        return ResponseEntity.status(HttpStatus.CREATED).body(repo.save(s));
    }

    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Submission> createWithFile(
            @RequestParam Long challengeId,
            @RequestParam String userHandle,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) MultipartFile file
    ) throws IOException {
        Submission s = new Submission();
        s.setChallengeId(challengeId);
        s.setUserHandle(userHandle != null ? userHandle : "guest");
        s.setDescription(description);
        s.setContentType(file != null ? "image" : "text");
        s.setContent(content);

        if (file != null && !file.isEmpty()) {
            Files.createDirectories(Path.of("/uploads"));
            String safeName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path target = Path.of("/uploads", safeName);

            // Redimensionne et compresse avant de sauvegarder
            try (InputStream in = file.getInputStream();
                 OutputStream out = Files.newOutputStream(target)) {
                Thumbnails.of(in)
                        .size(800, 800)          // max largeur/hauteur
                        .outputQuality(0.8)      // compression (0.0 à 1.0)
                        .toOutputStream(out);
            }

            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            s.setImageUrl("/uploads/" + safeName);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(repo.save(s));
    }


    @PatchMapping("/{id}/like")
    public ResponseEntity<Submission> like(@PathVariable Long id){
        return repo.findById(id)
                .map(s -> { s.setLikesCount(s.getLikesCount()+1); return ResponseEntity.ok(repo.save(s)); })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


}
