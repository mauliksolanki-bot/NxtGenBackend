package com.nxtgen.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "NXTGEN_SR_APPROVALS")
public class SrApprovalEntity {
    @Column(name = "COMMENTS", columnDefinition = "LONGTEXT")
    private String comments;
    @Column(name = "CREATED_AT")
    private java.time.LocalDateTime createdAt;

    @jakarta.persistence.PrePersist
    public void initializeCreatedAt() {
        if (createdAt == null) createdAt = java.time.LocalDateTime.now();
    }
    public String getComments() { return comments; }
    public void setComments(String value) { comments = value; }
    public java.time.LocalDateTime getCreatedAt() { return createdAt; }
    public void setApprovalStatus(String value) { approvalStatus = value; }
    public void setApprovalPendingTo(String value) { approvalPendingTo = value; }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "SR_ID", nullable = false)
    private String srId;

    @Column(name = "REQ_ID")
    private String reqId;

    @Column(name = "APPROVAL_LEVEL", nullable = false)
    private Integer approvalLevel;

    @Column(name = "APROVAL_STATUS")
    private String approvalStatus;

    @Column(name = "APPROVAL_PENDING_TO")
    private String approvalPendingTo;

    @Column(name = "APPROVAL_NAME")
    private String approvalName;
    @Column(name = "APPROVER_ROLE")
    private String approverRole;
    @Column(name = "APPROVER_USER_ID")
    private Long approverUserId;
    @Column(name = "APPROVER_GROUP_ID")
    private Long approverGroupId;
    public Long getApproverGroupId() { return approverGroupId; }
    public void setApproverGroupId(Long value) { approverGroupId = value; }

    public String getApprovalName() { return approvalName; }
    public String getApproverRole() { return approverRole; }
    public Long getApproverUserId() { return approverUserId; }
    public void setApprovalName(String value) { approvalName = value; }
    public void setApproverRole(String value) { approverRole = value; }
    public void setApproverUserId(Long value) { approverUserId = value; }

    public SrApprovalEntity() {
    }

    public SrApprovalEntity(String srId, String reqId, Integer approvalLevel, String approvalStatus, String approvalPendingTo) {
        this.srId = srId;
        this.reqId = reqId;
        this.approvalLevel = approvalLevel;
        this.approvalStatus = approvalStatus;
        this.approvalPendingTo = approvalPendingTo;
    }

    public Long getId() {
        return id;
    }

    public String getSrId() {
        return srId;
    }

    public String getReqId() {
        return reqId;
    }

    public Integer getApprovalLevel() {
        return approvalLevel;
    }

    public String getApprovalStatus() {
        return approvalStatus;
    }

    public String getApprovalPendingTo() {
        return approvalPendingTo;
    }
}
