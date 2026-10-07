package com.nxtgen.api.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.nxtgen.api.dto.CreateUserRequest;
import com.nxtgen.api.dto.CreateUserResponse;
import com.nxtgen.api.dto.ValidationResponse;
import com.nxtgen.api.entity.RoleEntity;
import com.nxtgen.api.entity.UserMasterEntity;
import com.nxtgen.api.entity.UserRoleEntity;
import com.nxtgen.api.exception.UserNotFoundException;
import com.nxtgen.api.exception.UserValidationException;
import com.nxtgen.api.repository.RoleRepository;
import com.nxtgen.api.repository.UserMasterRepository;
import com.nxtgen.api.repository.UserRoleRepository;

import static com.nxtgen.api.constant.NxtGenCommonConstant.*;

@Service
public class UserManagementService {

    private static final String EMAIL_DOMAIN = "@nxtgen.com";
    private static final Pattern NON_ALPHANUMERIC = Pattern.compile("[^a-z0-9]");
    private static final String VALIDATION_FAILED_MESSAGE = "Please correct the highlighted fields.";
    private static final String USER_NOT_FOUND_MESSAGE = "User not found.";

    private final UserMasterRepository userMasterRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserManagementService(
            UserMasterRepository userMasterRepository,
            UserRoleRepository userRoleRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userMasterRepository = userMasterRepository;
        this.userRoleRepository = userRoleRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public ValidationResponse validateUserData(CreateUserRequest request) {
        Map<String, String> errors = collectValidationErrors(request);
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
        String emailAddress = buildEmailAddress(firstName, lastName);
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
                request.getIsSuperAdmin()
        ));

        userRoleRepository.save(new UserRoleEntity(
                savedUser.getId(),
                savedUser.getUsername(),
                request.getRoleId(),
                SYSTEM_USER,
                LocalDateTime.now()
        ));

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
        userMasterRepository.delete(user);
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

        if (request.getRoleId() == null) {
            errors.put("roleId", "Role is required.");
        } else {
            roleRepository.findByIdAndIsActive(request.getRoleId(), ENABLED_FLAG)
                    .map(RoleEntity::getId)
                    .or(() -> {
                        errors.put("roleId", "Selected role is invalid or inactive.");
                        return Optional.empty();
                    });
        }

        if (!"Y".equalsIgnoreCase(request.getIsSuperAdmin()) && !"N".equalsIgnoreCase(request.getIsSuperAdmin())) {
            errors.put("isSuperAdmin", "Please select Yes or No for Super Admin.");
        }

        if (!firstName.isEmpty() && !lastName.isEmpty()) {
            String candidateEmail = buildEmailAddress(firstName, lastName);

            if (userMasterRepository.existsByEmailAddress(candidateEmail)) {
                errors.put("emailAddress", "A user with this email address already exists.");
            }
        }

        return errors;
    }

    private String buildEmployeeName(String firstName, String lastName) {
        return (firstName + " " + lastName).trim();
    }

    private String buildEmailAddress(String firstName, String lastName) {
        return normalize(firstName) + "." + normalize(lastName) + EMAIL_DOMAIN;
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
