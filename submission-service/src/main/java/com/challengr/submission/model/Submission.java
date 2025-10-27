package com.challengr.submission.model;
import jakarta.persistence.*;
import java.time.*;

@Entity public class Submission {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id;
    Long challengeId;
    String userHandle;
    String contentType; // "text" | "image"
    String imageUrl;        // URL d'image (MVP)
    String description;     // texte descriptif
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

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }


}
