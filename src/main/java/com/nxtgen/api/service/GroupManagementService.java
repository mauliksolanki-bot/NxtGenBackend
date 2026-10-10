package com.nxtgen.api.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.nxtgen.api.dto.CreateGroupRequest;
import com.nxtgen.api.dto.CreateGroupResponse;
import com.nxtgen.api.dto.GroupDetailResponse;
import com.nxtgen.api.dto.OptionResponse;
import com.nxtgen.api.dto.UpdateGroupRequest;
import com.nxtgen.api.entity.GroupEntity;
import com.nxtgen.api.entity.UserMasterEntity;
import com.nxtgen.api.exception.GroupNotFoundException;
import com.nxtgen.api.exception.GroupValidationException;
import com.nxtgen.api.repository.GroupRepository;
import com.nxtgen.api.repository.UserMasterRepository;

@Service
public class GroupManagementService {

    private static final String VALIDATION_FAILED_MESSAGE = "Please correct the highlighted fields.";
    private static final String GROUP_OWNER_ROLE_NAME = "GROUP OWNER";
    private static final String GROUP_NOT_FOUND_MESSAGE = "Group not found.";

    private final GroupRepository groupRepository;
    private final UserMasterRepository userMasterRepository;

    public GroupManagementService(GroupRepository groupRepository, UserMasterRepository userMasterRepository) {
        this.groupRepository = groupRepository;
        this.userMasterRepository = userMasterRepository;
    }

    public CreateGroupResponse createGroup(CreateGroupRequest request) {
        Map<String, String> errors = collectValidationErrors(request);

        if (!errors.isEmpty()) {
            throw new GroupValidationException(VALIDATION_FAILED_MESSAGE, errors);
        }

        String grpName = request.getGrpName().trim();
        String grpDescription = Optional.ofNullable(request.getGrpDescription()).map(String::trim).orElse(null);
        String groupOwner = Optional.ofNullable(request.getGroupOwner()).map(String::trim).filter(value -> !value.isEmpty()).orElse(null);

        GroupEntity savedGroup = groupRepository.save(new GroupEntity(grpName, grpDescription, groupOwner, request.getIsActive()));

        return new CreateGroupResponse(savedGroup.getId(), savedGroup.getGrpName(), "Group created successfully.");
    }

    /**
     * Powers the Create Group form's "Owner" search-and-select field: only
     * active users holding the "Group Owner" role are eligible candidates.
     */
    public List<OptionResponse> searchGroupOwners(String query) {
        String trimmedQuery = Optional.ofNullable(query).map(String::trim).orElse("");

        return userMasterRepository.searchActiveUsersByRoleNameAndNameLike(GROUP_OWNER_ROLE_NAME, trimmedQuery)
                .stream()
                .map(this::toOwnerOption)
                .collect(Collectors.toList());
    }

    /**
     * Powers the Create/Edit User form's "Group" search-multiselect field:
     * only active groups matching the typed query are eligible candidates.
     */
    public List<OptionResponse> searchGroups(String query) {
        String trimmedQuery = Optional.ofNullable(query).map(String::trim).orElse("");

        return groupRepository.searchActiveByNameLike(trimmedQuery)
                .stream()
                .map(this::toGroupOption)
                .collect(Collectors.toList());
    }

    public GroupDetailResponse getGroupById(Long id) {
        GroupEntity group = groupRepository.findById(id)
                .orElseThrow(() -> new GroupNotFoundException(GROUP_NOT_FOUND_MESSAGE));

        return new GroupDetailResponse(
                group.getId(),
                group.getGrpName(),
                group.getGrpDescription(),
                group.getGroupOwner(),
                group.getIsActive()
        );
    }

    public CreateGroupResponse updateGroup(UpdateGroupRequest request) {
        GroupEntity group = groupRepository.findById(request.getId())
                .orElseThrow(() -> new GroupNotFoundException(GROUP_NOT_FOUND_MESSAGE));

        Map<String, String> errors = collectUpdateValidationErrors(request);

        if (!errors.isEmpty()) {
            throw new GroupValidationException(VALIDATION_FAILED_MESSAGE, errors);
        }

        group.setGrpName(request.getGrpName().trim());
        group.setGrpDescription(Optional.ofNullable(request.getGrpDescription()).map(String::trim).orElse(null));
        group.setGroupOwner(Optional.ofNullable(request.getGroupOwner()).map(String::trim).filter(value -> !value.isEmpty()).orElse(null));
        group.setIsActive(request.getIsActive());

        GroupEntity savedGroup = groupRepository.save(group);

        return new CreateGroupResponse(savedGroup.getId(), savedGroup.getGrpName(), "Group updated successfully.");
    }

    private OptionResponse toOwnerOption(UserMasterEntity user) {
        return new OptionResponse(user.getEmplNm(), user.getEmplNm());
    }

    private OptionResponse toGroupOption(GroupEntity group) {
        return new OptionResponse(String.valueOf(group.getId()), group.getGrpName());
    }

    private Map<String, String> collectValidationErrors(CreateGroupRequest request) {
        Map<String, String> errors = new HashMap<>();

        String grpName = Optional.ofNullable(request.getGrpName()).map(String::trim).orElse("");

        if (grpName.isEmpty()) {
            errors.put("grpName", "Group name is required.");
        } else if (groupRepository.existsByGrpNameIgnoreCase(grpName)) {
            errors.put("grpName", "A group with this name already exists.");
        }

        if (!"Y".equalsIgnoreCase(request.getIsActive()) && !"N".equalsIgnoreCase(request.getIsActive())) {
            errors.put("isActive", "Please select Yes or No for Active.");
        }

        return errors;
    }

    private Map<String, String> collectUpdateValidationErrors(UpdateGroupRequest request) {
        Map<String, String> errors = new HashMap<>();

        String grpName = Optional.ofNullable(request.getGrpName()).map(String::trim).orElse("");

        if (grpName.isEmpty()) {
            errors.put("grpName", "Group name is required.");
        } else if (groupRepository.existsByGrpNameIgnoreCaseAndIdNot(grpName, request.getId())) {
            errors.put("grpName", "A group with this name already exists.");
        }

        if (!"Y".equalsIgnoreCase(request.getIsActive()) && !"N".equalsIgnoreCase(request.getIsActive())) {
            errors.put("isActive", "Please select Yes or No for Active.");
        }

        return errors;
    }
}
