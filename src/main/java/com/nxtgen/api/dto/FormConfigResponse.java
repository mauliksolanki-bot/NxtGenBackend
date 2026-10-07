package com.nxtgen.api.dto;

import java.util.List;

public class FormConfigResponse {

    private final Long id;
    private final String formName;
    private final String dataApi;
    private final String templateType;
    private final List<FormFieldResponse> fields;

    public FormConfigResponse(Long id, String formName, String dataApi, String templateType, List<FormFieldResponse> fields) {
        this.id = id;
        this.formName = formName;
        this.dataApi = dataApi;
        this.templateType = templateType;
        this.fields = fields;
    }

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

    public List<FormFieldResponse> getFields() {
        return fields;
    }
}
