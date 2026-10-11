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
