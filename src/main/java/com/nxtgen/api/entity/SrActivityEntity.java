package com.nxtgen.api.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "NXTGEN_SR_ACTIVITY")
public class SrActivityEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "SR_ID", nullable = false) private String srId;
    @Column(name = "ACTIVITY_TYPE", nullable = false) private String type;
    @Column(name = "AUTHOR", nullable = false) private String author;
    @Column(name = "BODY", columnDefinition = "LONGTEXT", nullable = false) private String body;
    @Column(name = "CREATED_AT", nullable = false) private LocalDateTime createdAt;

    public SrActivityEntity() {}
    public SrActivityEntity(String srId, String type, String author, String body) {
        this.srId = srId;
        this.type = type;
        this.author = author;
        this.body = body;
        this.createdAt = LocalDateTime.now();
    }
    public Long getId() { return id; }
    public String getType() { return type; }
    public String getAuthor() { return author; }
    public String getBody() { return body; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
