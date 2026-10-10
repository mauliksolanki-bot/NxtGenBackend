package com.nxtgen.api.service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.nxtgen.api.dto.FormConfigResponse;
import com.nxtgen.api.dto.FormFieldResponse;
import com.nxtgen.api.dto.OptionResponse;
import com.nxtgen.api.entity.FormEntity;
import com.nxtgen.api.entity.FormFieldEntity;
import com.nxtgen.api.entity.RoleEntity;
import com.nxtgen.api.exception.FormNotFoundException;
import com.nxtgen.api.repository.FormFieldRepository;
import com.nxtgen.api.repository.FormRepository;
import com.nxtgen.api.repository.RoleRepository;

import static com.nxtgen.api.constant.NxtGenCommonConstant.*;

/**
 * Common endpoint support for fetching form + form-field metadata
 * (NXTGEN_FORM / NXTGEN_FORM_FIELDS), mirroring the Grid config pattern so
 * forms like Create User are rendered from configuration, not hard-coded.
 */
@Service
public class FormConfigService {

    private static final String FORM_NOT_FOUND_MESSAGE = "Form configuration is unavailable right now.";
    private static final List<OptionResponse> YES_NO_OPTIONS = List.of(
            new OptionResponse("Y", "Yes"),
            new OptionResponse("N", "No")
    );

    private final FormRepository formRepository;
    private final FormFieldRepository formFieldRepository;
    private final RoleRepository roleRepository;

    public FormConfigService(FormRepository formRepository, FormFieldRepository formFieldRepository, RoleRepository roleRepository) {
        this.formRepository = formRepository;
        this.formFieldRepository = formFieldRepository;
        this.roleRepository = roleRepository;
    }

    public FormConfigResponse getFormConfig(String formName) {
        FormEntity form = formRepository.findByFormName(formName)
                .orElseThrow(() -> new FormNotFoundException(FORM_NOT_FOUND_MESSAGE));

        List<FormFieldResponse> fields = formFieldRepository.findByFormIdOrderByDisplayOrderAsc(form.getId())
                .stream()
                .map(this::toFieldResponse)
                .collect(Collectors.toList());

        return new FormConfigResponse(
                form.getId(),
                form.getFormName(),
                form.getDataApi(),
                form.getTemplateType(),
                form.getSubmitLabel(),
                fields
        );
    }

    private FormFieldResponse toFieldResponse(FormFieldEntity field) {
        return new FormFieldResponse(
                field.getId(),
                field.getFieldName(),
                field.getFieldLabel(),
                field.getDataField(),
                field.getFieldType(),
                field.getPlaceholder(),
                MANDATORY_FLAG.equalsIgnoreCase(field.getIsMandatory()),
                MANDATORY_FLAG.equalsIgnoreCase(field.getIsReadonly()),
                field.getDefaultValue(),
                field.getDisplayOrder(),
                resolveOptions(field.getOptionsSource())
        );
    }

    private List<OptionResponse> resolveOptions(String optionsSource) {
        if (ROLE_OPTIONS_SOURCE.equalsIgnoreCase(optionsSource)) {
            return roleRepository.findByIsActiveOrderByRoleNameAsc(ENABLED_FLAG)
                    .stream()
                    .map(this::toRoleOption)
                    .collect(Collectors.toList());
        }

        if (YES_NO_OPTIONS_SOURCE.equalsIgnoreCase(optionsSource)) {
            return YES_NO_OPTIONS;
        }

        return List.of();
    }

    private OptionResponse toRoleOption(RoleEntity role) {
        return new OptionResponse(String.valueOf(role.getId()), toTitleCase(role.getRoleName()));
    }

    private String toTitleCase(String value) {
        return Arrays.stream(value.trim().toLowerCase().split("\\s+"))
                .filter(word -> !word.isEmpty())
                .map(word -> Character.toUpperCase(word.charAt(0)) + word.substring(1))
                .collect(Collectors.joining(" "));
    }
}
