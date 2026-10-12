package com.nxtgen.api.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

@Entity
@Table(name = "NXTGEN_SR_ATTACHMENTS")
public class SrAttachmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "SR_ID", nullable = false)
    private String srId;

    @Column(name = "FILE_NAME", nullable = false)
    private String fileName;

    @Column(name = "CONTENT_TYPE")
    private String contentType;

    @Column(name = "FILE_SIZE", nullable = false)
    private Long fileSize;

    @Lob
    @Column(name = "FILE_DATA", nullable = false, columnDefinition = "LONGBLOB")
    private byte[] fileData;

    @Column(name = "UPLOADED_BY")
    private String uploadedBy;

    @Column(name = "UPLOADED_AT")
    private LocalDateTime uploadedAt;

    public SrAttachmentEntity() {
    }

    public SrAttachmentEntity(
            String srId,
            String fileName,
            String contentType,
            Long fileSize,
            byte[] fileData,
            String uploadedBy,
            LocalDateTime uploadedAt
    ) {
        this.srId = srId;
        this.fileName = fileName;
        this.contentType = contentType;
        this.fileSize = fileSize;
        this.fileData = fileData;
        this.uploadedBy = uploadedBy;
        this.uploadedAt = uploadedAt;
    }

    public Long getId() {
        return id;
    }

    public String getSrId() {
        return srId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public byte[] getFileData() {
        return fileData;
    }
}
