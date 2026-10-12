package com.nxtgen.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "NXTGEN_SR_APPROVAL_CHECKS")
public class SrApprovalCheckEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "REQUEST_TYPE", nullable = false)
    private String requestType;

    @Column(name = "SUBCATEGORY")
    private String subcategory;

    @Column(name = "APPROVAL_LEVEL", nullable = false)
    private Integer approvalLevel;

    @Column(name = "APPROVAL_NAME", nullable = false)
    private String approvalName;

    @Column(name = "APPROVER_ROLE")
    private String approverRole;
    @Column(name = "APPROVER_GROUP_ID")
    private Long approverGroupId;
    public Long getApproverGroupId() { return approverGroupId; }

    @Column(name = "IS_ACTIVE", nullable = false)
    private String isActive;

    public Long getId() {
        return id;
    }

    public String getRequestType() {
        return requestType;
    }

    public Integer getApprovalLevel() {
        return approvalLevel;
    }

    public String getApprovalName() {
        return approvalName;
    }

    public String getApproverRole() {
        return approverRole;
    }

    public String getIsActive() {
        return isActive;
    }
}
