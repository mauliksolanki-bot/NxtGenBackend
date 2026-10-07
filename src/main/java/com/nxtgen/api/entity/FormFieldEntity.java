package com.nxtgen.api.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "NXTGEN_FORM_FIELDS")
public class FormFieldEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "FORM_ID", nullable = false)
    private Long formId;

    @Column(name = "TEMPLATE_TYPE")
    private String templateType;

    @Column(name = "DISPLAY_ORDER")
    private Long displayOrder;

    @Column(name = "FIELD_NAME")
    private String fieldName;

    @Column(name = "FIELD_LABEL")
    private String fieldLabel;

    @Column(name = "DATA_FIELD")
    private String dataField;

    @Column(name = "FIELD_TYPE")
    private String fieldType;

    @Column(name = "PLACEHOLDER")
    private String placeholder;

    @Column(name = "IS_MANDATORY")
    private String isMandatory;

    @Column(name = "IS_READONLY")
    private String isReadonly;

    @Column(name = "DEFAULT_VALUE")
    private String defaultValue;

    @Column(name = "OPTIONS_SOURCE")
    private String optionsSource;

    @Column(name = "CRE_BY")
    private String creBy;

    @Column(name = "CRE_DATE")
    private LocalDateTime creDate;

    public Long getId() {
        return id;
    }

    public Long getFormId() {
        return formId;
    }

    public String getTemplateType() {
        return templateType;
    }

    public Long getDisplayOrder() {
        return displayOrder;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getFieldLabel() {
        return fieldLabel;
    }

    public String getDataField() {
        return dataField;
    }

    public String getFieldType() {
        return fieldType;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public String getIsMandatory() {
        return isMandatory;
    }

    public String getIsReadonly() {
        return isReadonly;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public String getOptionsSource() {
        return optionsSource;
    }

    public String getCreBy() {
        return creBy;
    }

    public LocalDateTime getCreDate() {
        return creDate;
    }
}
