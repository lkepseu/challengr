package com.challengr.submission.api;
import org.springframework.web.bind.annotation.*; import org.springframework.http.*;
import com.challengr.submission.repo.SubmissionRepo; import com.challengr.submission.model.Submission;
import java.util.*;

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
        if(s.getChallengeId()==null || s.getContent()==null) return ResponseEntity.badRequest().build();
        if(s.getUserHandle()==null) s.setUserHandle("guest");
        if(s.getContentType()==null) s.setContentType("text");
        return ResponseEntity.status(HttpStatus.CREATED).body(repo.save(s));
    }
}
