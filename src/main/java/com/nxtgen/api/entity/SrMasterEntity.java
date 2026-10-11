package com.nxtgen.api.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "NXTGEN_SR_MASTER")
public class SrMasterEntity {

    @Id
    @Column(name = "SR_ID")
    private String srId;

    @Column(name = "REQ_ID", nullable = false)
    private String reqId;

    @Column(name = "REQUEST_TYPE")
    private String requestType;

    @Column(name = "SUBCATEGORY_ID")
    private Long subcategoryId;

    @Column(name = "SUBCATEGORY")
    private String subcategory;

    @Column(name = "TITLE")
    private String title;

    public Long getSubcategoryId() {
        return subcategoryId;
    }

    public void setSubcategoryId(Long subcategoryId) {
        this.subcategoryId = subcategoryId;
    }

    public String getSubcategory() {
        return subcategory;
    }

    public void setSubcategory(String subcategory) {
        this.subcategory = subcategory;
    }

    @Column(name = "REQUESTED_FOR")
    private String requestedFor;

    @Column(name = "DUE_DATE")
    private LocalDate dueDate;

    @Column(name = "CI_ITEM")
    private String ciItem;

    @Column(name = "OPENED_AT")
    private LocalDateTime openedAt;

    @Column(name = "OPENED_BY")
    private String openedBy;

    @Column(name = "OPENED_BY_USERNAME")
    private String openedByUsername;

    @Column(name = "STAGE")
    private String stage;

    @Column(name = "STATE")
    private String state;

    @Column(name = "QUANTITY")
    private Integer quantity;

    @Column(name = "ESTIMATED_DELIVERY")
    private LocalDate estimatedDelivery;

    @Column(name = "CRE_BY")
    private String creBy;

    @Column(name = "CRE_DATE")
    private LocalDateTime creDate;

    @Column(name = "UPD_BY")
    private String updBy;

    @Column(name = "UPD_DATE")
    private LocalDateTime updDate;

    public String getSrId() {
        return srId;
    }

    public void setSrId(String srId) {
        this.srId = srId;
    }

    public String getReqId() {
        return reqId;
    }

    public void setReqId(String reqId) {
        this.reqId = reqId;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRequestedFor() {
        return requestedFor;
    }

    public void setRequestedFor(String requestedFor) {
        this.requestedFor = requestedFor;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public String getCiItem() {
        return ciItem;
    }

    public void setCiItem(String ciItem) {
        this.ciItem = ciItem;
    }

    public LocalDateTime getOpenedAt() {
        return openedAt;
    }

    public void setOpenedAt(LocalDateTime openedAt) {
        this.openedAt = openedAt;
    }

    public String getOpenedBy() {
        return openedBy;
    }

    public void setOpenedBy(String openedBy) {
        this.openedBy = openedBy;
    }

    public String getOpenedByUsername() {
        return openedByUsername;
    }

    public void setOpenedByUsername(String openedByUsername) {
        this.openedByUsername = openedByUsername;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public LocalDate getEstimatedDelivery() {
        return estimatedDelivery;
    }

    public void setEstimatedDelivery(LocalDate estimatedDelivery) {
        this.estimatedDelivery = estimatedDelivery;
    }

    public String getCreBy() {
        return creBy;
    }

    public void setCreBy(String creBy) {
        this.creBy = creBy;
    }

    public LocalDateTime getCreDate() {
        return creDate;
    }

    public void setCreDate(LocalDateTime creDate) {
        this.creDate = creDate;
    }

    public String getUpdBy() {
        return updBy;
    }

    public void setUpdBy(String updBy) {
        this.updBy = updBy;
    }

    public LocalDateTime getUpdDate() {
        return updDate;
    }

    public void setUpdDate(LocalDateTime updDate) {
        this.updDate = updDate;
    }
}
