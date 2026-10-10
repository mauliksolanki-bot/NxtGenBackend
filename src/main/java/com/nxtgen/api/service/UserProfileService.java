package com.nxtgen.api.service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.nxtgen.api.dto.UserProfileResponse;
import com.nxtgen.api.entity.GroupEntity;
import com.nxtgen.api.entity.RoleEntity;
import com.nxtgen.api.entity.UserGroupEntity;
import com.nxtgen.api.entity.UserMasterEntity;
import com.nxtgen.api.entity.UserRoleEntity;
import com.nxtgen.api.exception.AuthenticationFailedException;
import com.nxtgen.api.repository.GroupRepository;
import com.nxtgen.api.repository.RoleRepository;
import com.nxtgen.api.repository.UserGroupRepository;
import com.nxtgen.api.repository.UserMasterRepository;
import com.nxtgen.api.repository.UserRoleRepository;
import com.nxtgen.api.security.JwtTokenProvider;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;

@Service
public class UserProfileService {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String INVALID_TOKEN_MESSAGE = "Your session could not be verified.";

    private final JwtTokenProvider jwtTokenProvider;
    private final UserMasterRepository userMasterRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;
    private final UserGroupRepository userGroupRepository;
    private final GroupRepository groupRepository;

    public UserProfileService(
            JwtTokenProvider jwtTokenProvider,
            UserMasterRepository userMasterRepository,
            UserRoleRepository userRoleRepository,
            RoleRepository roleRepository,
            UserGroupRepository userGroupRepository,
            GroupRepository groupRepository
    ) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userMasterRepository = userMasterRepository;
        this.userRoleRepository = userRoleRepository;
        this.roleRepository = roleRepository;
        this.userGroupRepository = userGroupRepository;
        this.groupRepository = groupRepository;
    }

    public UserProfileResponse getProfile(String authorizationHeader) {
        String token = Optional.ofNullable(authorizationHeader)
                .filter(header -> header.startsWith(BEARER_PREFIX))
                .map(header -> header.substring(BEARER_PREFIX.length()).trim())
                .filter(value -> !value.isEmpty())
                .orElseThrow(() -> new AuthenticationFailedException(INVALID_TOKEN_MESSAGE));

        Claims claims;
        try {
            claims = jwtTokenProvider.parseClaims(token);
        } catch (JwtException | IllegalArgumentException exception) {
            throw new AuthenticationFailedException(INVALID_TOKEN_MESSAGE);
        }

        if (claims.getExpiration() == null || claims.getExpiration().before(new java.util.Date())) {
            throw new AuthenticationFailedException(INVALID_TOKEN_MESSAGE);
        }

        UserMasterEntity user = userMasterRepository.findByUsername(claims.getSubject())
                .orElseThrow(() -> new AuthenticationFailedException(INVALID_TOKEN_MESSAGE));

        List<Long> roleIds = userRoleRepository.findByUserId(user.getId()).stream()
                .map(UserRoleEntity::getRoleId)
                .collect(Collectors.toList());
        List<Long> groupIds = userGroupRepository.findByUserId(user.getId()).stream()
                .map(UserGroupEntity::getGroupId)
                .collect(Collectors.toList());

        List<String> roles = roleRepository.findAllById(roleIds).stream()
                .map(RoleEntity::getRoleName)
                .sorted(Comparator.comparing(String::toLowerCase))
                .collect(Collectors.toList());
        List<String> groups = groupRepository.findAllById(groupIds).stream()
                .map(GroupEntity::getGrpName)
                .sorted(Comparator.comparing(String::toLowerCase))
                .collect(Collectors.toList());

        return new UserProfileResponse(user.getUsername(), roles, groups);
    }
}
