package com.nxtgen.api.dto;

import java.util.List;

public class FormFieldResponse {

    private final Long id;
    private final String fieldName;
    private final String fieldLabel;
    private final String dataField;
    private final String fieldType;
    private final String placeholder;
    private final boolean mandatory;
    private final boolean readOnly;
    private final String defaultValue;
    private final Long displayOrder;
    private final List<OptionResponse> options;

    public FormFieldResponse(
            Long id,
            String fieldName,
            String fieldLabel,
            String dataField,
            String fieldType,
            String placeholder,
            boolean mandatory,
            boolean readOnly,
            String defaultValue,
            Long displayOrder,
            List<OptionResponse> options
    ) {
        this.id = id;
        this.fieldName = fieldName;
        this.fieldLabel = fieldLabel;
        this.dataField = dataField;
        this.fieldType = fieldType;
        this.placeholder = placeholder;
        this.mandatory = mandatory;
        this.readOnly = readOnly;
        this.defaultValue = defaultValue;
        this.displayOrder = displayOrder;
        this.options = options;
    }

    public Long getId() {
        return id;
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

    public boolean isMandatory() {
        return mandatory;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public Long getDisplayOrder() {
        return displayOrder;
    }

    public List<OptionResponse> getOptions() {
        return options;
    }
}
