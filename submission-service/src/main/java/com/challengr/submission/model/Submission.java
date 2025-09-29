package com.challengr.submission.model;
import jakarta.persistence.*;
import java.time.*;

@Entity public class Submission {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id;
    Long challengeId;
    String userHandle;
    String contentType; // "text" | "image"
    @Column(length=4000) String content; // text or image URL
    Integer likesCount = 0;
    LocalDateTime createdAt = LocalDateTime.now();

    // getters/setters
    public Long getId(){return id;} public void setId(Long id){this.id=id;}
    public Long getChallengeId(){return challengeId;} public void setChallengeId(Long c){this.challengeId=c;}
    public String getUserHandle(){return userHandle;} public void setUserHandle(String u){this.userHandle=u;}
    public String getContentType(){return contentType;} public void setContentType(String t){this.contentType=t;}
    public String getContent(){return content;} public void setContent(String c){this.content=c;}
    public Integer getLikesCount(){return likesCount;} public void setLikesCount(Integer l){this.likesCount=l;}
    public LocalDateTime getCreatedAt(){return createdAt;} public void setCreatedAt(LocalDateTime t){this.createdAt=t;}
}
