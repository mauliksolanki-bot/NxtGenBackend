package com.nxtgen.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "NXTGEN_SR_LOOKUP")
public class SrLookupEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "LOOKUP_TYPE", nullable = false)
    private String lookupType;

    @Column(name = "LOOKUP_CODE", nullable = false)
    private String lookupCode;

    @Column(name = "LOOKUP_LABEL", nullable = false)
    private String lookupLabel;

    @Column(name = "DISPLAY_ORDER", nullable = false)
    private Integer displayOrder;

    @Column(name = "IS_ACTIVE", nullable = false)
    private String isActive;

    public Long getId() {
        return id;
    }

    public String getLookupType() {
        return lookupType;
    }

    public String getLookupCode() {
        return lookupCode;
    }

    public String getLookupLabel() {
        return lookupLabel;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public String getIsActive() {
        return isActive;
    }
}
