package com.nxtgen.api.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "NXTGEN_FORM")
public class FormEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "FORM_NAME", nullable = false)
    private String formName;

    @Column(name = "DATA_API")
    private String dataApi;

    @Column(name = "TEMPLATE_TYPE")
    private String templateType;

    @Column(name = "SUBMIT_LABEL")
    private String submitLabel;

    @Column(name = "CRE_BY")
    private String creBy;

    @Column(name = "CRE_DATE")
    private LocalDateTime creDate;

    public Long getId() {
        return id;
    }

    public String getFormName() {
        return formName;
    }

    public String getDataApi() {
        return dataApi;
    }

    public String getTemplateType() {
        return templateType;
    }

    public String getSubmitLabel() {
        return submitLabel;
    }

    public String getCreBy() {
        return creBy;
    }

    public LocalDateTime getCreDate() {
        return creDate;
    }
}
