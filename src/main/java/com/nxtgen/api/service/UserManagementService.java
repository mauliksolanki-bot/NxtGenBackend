package com.nxtgen.api.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.nxtgen.api.dto.CreateUserRequest;
import com.nxtgen.api.dto.CreateUserResponse;
import com.nxtgen.api.dto.OptionResponse;
import com.nxtgen.api.dto.UpdateUserRequest;
import com.nxtgen.api.dto.UserDetailResponse;
import com.nxtgen.api.dto.ValidationResponse;
import com.nxtgen.api.entity.GroupEntity;
import com.nxtgen.api.entity.UserGroupEntity;
import com.nxtgen.api.entity.UserMasterEntity;
import com.nxtgen.api.entity.UserRoleEntity;
import com.nxtgen.api.exception.UserNotFoundException;
import com.nxtgen.api.exception.UserValidationException;
import com.nxtgen.api.repository.GroupRepository;
import com.nxtgen.api.repository.RoleRepository;
import com.nxtgen.api.repository.UserGroupRepository;
import com.nxtgen.api.repository.UserMasterRepository;
import com.nxtgen.api.repository.UserRoleRepository;

import static com.nxtgen.api.constant.NxtGenCommonConstant.*;

@Service
public class UserManagementService {

    private static final Pattern NON_ALPHANUMERIC = Pattern.compile("[^a-z0-9]");
    private static final String VALIDATION_FAILED_MESSAGE = "Please correct the highlighted fields.";
    private static final String USER_NOT_FOUND_MESSAGE = "User not found.";
    private static final String USER_ALREADY_EXISTS_MESSAGE = "User is already exist in NxtGen";

    private final UserMasterRepository userMasterRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;
    private final UserGroupRepository userGroupRepository;
    private final GroupRepository groupRepository;
    private final PasswordEncoder passwordEncoder;

    public UserManagementService(
            UserMasterRepository userMasterRepository,
            UserRoleRepository userRoleRepository,
            RoleRepository roleRepository,
            UserGroupRepository userGroupRepository,
            GroupRepository groupRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userMasterRepository = userMasterRepository;
        this.userRoleRepository = userRoleRepository;
        this.roleRepository = roleRepository;
        this.userGroupRepository = userGroupRepository;
        this.groupRepository = groupRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public ValidationResponse validateUserData(CreateUserRequest request) {
        Map<String, String> errors = collectUserExistenceErrors(request);
        return new ValidationResponse(errors.isEmpty(), errors);
    }

    public CreateUserResponse createUser(CreateUserRequest request) {
        Map<String, String> errors = collectValidationErrors(request);

        if (!errors.isEmpty()) {
            throw new UserValidationException(VALIDATION_FAILED_MESSAGE, errors);
        }

        String firstName = request.getFirstName().trim();
        String lastName = request.getLastName().trim();
        String emplNm = buildEmployeeName(firstName, lastName);
        String emailAddress = request.getEmailAddress().trim();
        String username = resolveUniqueUsername(buildUsernameBase(firstName, lastName), 0);
        String rawPassword = Optional.ofNullable(request.getPassword())
                .filter(password -> !password.isBlank())
                .orElse(DEFAULT_USER_PASSWORD);

        UserMasterEntity savedUser = userMasterRepository.save(new UserMasterEntity(
                firstName,
                lastName,
                emplNm,
                username,
                passwordEncoder.encode(rawPassword),
                emailAddress,
                ACTIVE_FLAG,
                "N",
                request.getIsSuperAdmin(),
                request.getUserId()
        ));

        userRoleRepository.saveAll(request.getRoleIds().stream()
                .map(roleId -> new UserRoleEntity(
                        savedUser.getId(),
                        savedUser.getUsername(),
                        roleId,
                        SYSTEM_USER,
                        LocalDateTime.now()
                ))
                .collect(Collectors.toList()));

        userGroupRepository.saveAll(request.getGroupIds().stream()
                .map(groupId -> new UserGroupEntity(
                        savedUser.getId(),
                        savedUser.getUsername(),
                        groupId,
                        SYSTEM_USER,
                        LocalDateTime.now()
                ))
                .collect(Collectors.toList()));

        return new CreateUserResponse(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmplNm(),
                savedUser.getEmailAddress(),
                "User created successfully."
        );
    }

    public void deleteUser(Long userId) {
        UserMasterEntity user = userMasterRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_MESSAGE));

        userRoleRepository.deleteByUserId(user.getId());
        userGroupRepository.deleteByUserId(user.getId());
        userMasterRepository.delete(user);
    }

    public UserDetailResponse getUserById(Long id) {
        UserMasterEntity user = userMasterRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_MESSAGE));

        List<Long> roleIds = userRoleRepository.findByUserId(user.getId()).stream()
                .map(UserRoleEntity::getRoleId)
                .collect(Collectors.toList());

        List<Long> groupIds = userGroupRepository.findByUserId(user.getId()).stream()
                .map(UserGroupEntity::getGroupId)
                .collect(Collectors.toList());

        Map<Long, String> groupNamesById = groupRepository.findAllById(groupIds).stream()
                .collect(Collectors.toMap(GroupEntity::getId, GroupEntity::getGrpName));

        List<OptionResponse> groups = groupIds.stream()
                .map(groupId -> new OptionResponse(
                        String.valueOf(groupId),
                        groupNamesById.getOrDefault(groupId, String.valueOf(groupId))
                ))
                .collect(Collectors.toList());

        return new UserDetailResponse(
                user.getId(),
                user.getSrcUserId(),
                user.getFirstNm(),
                user.getLastNm(),
                user.getEmplNm(),
                user.getUsername(),
                user.getEmailAddress(),
                roleIds,
                groups,
                user.getIsSupAdmin()
        );
    }

    public CreateUserResponse updateUser(UpdateUserRequest request) {
        UserMasterEntity user = userMasterRepository.findById(request.getId())
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_MESSAGE));

        Map<String, String> errors = collectUpdateValidationErrors(request);

        if (!errors.isEmpty()) {
            throw new UserValidationException(VALIDATION_FAILED_MESSAGE, errors);
        }

        user.setFirstNm(request.getFirstName().trim());
        user.setLastNm(request.getLastName().trim());
        user.setEmplNm(buildEmployeeName(request.getFirstName().trim(), request.getLastName().trim()));
        user.setEmailAddress(request.getEmailAddress().trim());
        user.setIsSupAdmin(request.getIsSuperAdmin());

        UserMasterEntity savedUser = userMasterRepository.save(user);

        userRoleRepository.deleteByUserId(savedUser.getId());
        userRoleRepository.saveAll(request.getRoleIds().stream()
                .map(roleId -> new UserRoleEntity(
                        savedUser.getId(),
                        savedUser.getUsername(),
                        roleId,
                        SYSTEM_USER,
                        LocalDateTime.now()
                ))
                .collect(Collectors.toList()));

        userGroupRepository.deleteByUserId(savedUser.getId());
        userGroupRepository.saveAll(request.getGroupIds().stream()
                .map(groupId -> new UserGroupEntity(
                        savedUser.getId(),
                        savedUser.getUsername(),
                        groupId,
                        SYSTEM_USER,
                        LocalDateTime.now()
                ))
                .collect(Collectors.toList()));

        return new CreateUserResponse(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmplNm(),
                savedUser.getEmailAddress(),
                "User updated successfully."
        );
    }

    private Map<String, String> collectValidationErrors(CreateUserRequest request) {
        Map<String, String> errors = new HashMap<>();

        String firstName = Optional.ofNullable(request.getFirstName()).map(String::trim).orElse("");
        String lastName = Optional.ofNullable(request.getLastName()).map(String::trim).orElse("");

        if (firstName.isEmpty()) {
            errors.put("firstName", "First name is required.");
        }

        if (lastName.isEmpty()) {
            errors.put("lastName", "Last name is required.");
        }

        if (request.getRoleIds() == null || request.getRoleIds().isEmpty()) {
            errors.put("roleId", "At least one role is required.");
        } else if (hasInvalidRole(request.getRoleIds())) {
            errors.put("roleId", "One or more selected roles are invalid or inactive.");
        }

        if (request.getGroupIds() == null || request.getGroupIds().isEmpty()) {
            errors.put("groupIds", "At least one group is required.");
        } else if (hasInvalidGroup(request.getGroupIds())) {
            errors.put("groupIds", "One or more selected groups are invalid or inactive.");
        }

        if (!"Y".equalsIgnoreCase(request.getIsSuperAdmin()) && !"N".equalsIgnoreCase(request.getIsSuperAdmin())) {
            errors.put("isSuperAdmin", "Please select Yes or No for Super Admin.");
        }

        String emailAddress = Optional.ofNullable(request.getEmailAddress()).map(String::trim).orElse("");

        if (emailAddress.isEmpty()) {
            errors.put("emailAddress", "Please select a user from the User ID search results.");
        } else if (userMasterRepository.existsByEmailAddress(emailAddress)) {
            errors.put("emailAddress", "A user with this email address already exists.");
        }

        errors.putAll(collectUserExistenceErrors(request));

        return errors;
    }

    private Map<String, String> collectUpdateValidationErrors(UpdateUserRequest request) {
        Map<String, String> errors = new HashMap<>();

        String firstName = Optional.ofNullable(request.getFirstName()).map(String::trim).orElse("");
        String lastName = Optional.ofNullable(request.getLastName()).map(String::trim).orElse("");

        if (firstName.isEmpty()) {
            errors.put("firstName", "First name is required.");
        }

        if (lastName.isEmpty()) {
            errors.put("lastName", "Last name is required.");
        }

        if (request.getRoleIds() == null || request.getRoleIds().isEmpty()) {
            errors.put("roleId", "At least one role is required.");
        } else if (hasInvalidRole(request.getRoleIds())) {
            errors.put("roleId", "One or more selected roles are invalid or inactive.");
        }

        if (request.getGroupIds() == null || request.getGroupIds().isEmpty()) {
            errors.put("groupIds", "At least one group is required.");
        } else if (hasInvalidGroup(request.getGroupIds())) {
            errors.put("groupIds", "One or more selected groups are invalid or inactive.");
        }

        if (!"Y".equalsIgnoreCase(request.getIsSuperAdmin()) && !"N".equalsIgnoreCase(request.getIsSuperAdmin())) {
            errors.put("isSuperAdmin", "Please select Yes or No for Super Admin.");
        }

        String emailAddress = Optional.ofNullable(request.getEmailAddress()).map(String::trim).orElse("");

        if (emailAddress.isEmpty()) {
            errors.put("emailAddress", "Email address is required.");
        } else if (userMasterRepository.existsByEmailAddressAndIdNot(emailAddress, request.getId())) {
            errors.put("emailAddress", "A user with this email address already exists.");
        }

        if (request.getUserId() != null
                && userMasterRepository.existsBySrcUserIdAndIdNot(request.getUserId(), request.getId())) {
            errors.put("userId", USER_ALREADY_EXISTS_MESSAGE);
        }

        return errors;
    }

    /**
     * Checks only whether the selected User ID (sourced from
     * NXTGEN_USERS_MASTER_DATA_WH) has already been used to create a user in
     * NXTGEN_USERS_MASTER. Used standalone by /validateuserdata and merged
     * into the full validation set before actually creating a user.
     */
    private Map<String, String> collectUserExistenceErrors(CreateUserRequest request) {
        Map<String, String> errors = new HashMap<>();

        if (request.getUserId() != null && userMasterRepository.existsBySrcUserId(request.getUserId())) {
            errors.put("userId", USER_ALREADY_EXISTS_MESSAGE);
        }

        return errors;
    }

    private boolean hasInvalidRole(List<Long> roleIds) {
        return roleIds.stream()
                .anyMatch(roleId -> roleRepository.findByIdAndIsActive(roleId, ENABLED_FLAG).isEmpty());
    }

    private boolean hasInvalidGroup(List<Long> groupIds) {
        return groupIds.stream()
                .anyMatch(groupId -> !groupRepository.existsByIdAndIsActive(groupId, ENABLED_FLAG));
    }

    private String buildEmployeeName(String firstName, String lastName) {
        return (firstName + " " + lastName).trim();
    }

    private String buildUsernameBase(String firstName, String lastName) {
        String base = normalize(firstName).substring(0, 1) + normalize(lastName);
        return base.isEmpty() ? "user" : base;
    }

    private String normalize(String value) {
        return NON_ALPHANUMERIC.matcher(value.toLowerCase()).replaceAll("");
    }

    /**
     * Recursively (no for-loops) finds the first available username by
     * appending a numeric suffix until no conflicting record exists.
     */
    private String resolveUniqueUsername(String base, int suffix) {
        String candidate = suffix == 0 ? base : base + suffix;

        return userMasterRepository.existsByUsername(candidate)
                ? resolveUniqueUsername(base, suffix + 1)
                : candidate;
    }
}
