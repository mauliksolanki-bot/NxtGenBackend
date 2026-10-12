package com.nxtgen.api.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.nxtgen.api.dto.NavMenuResponse;
import com.nxtgen.api.entity.NavMenuEntity;
import com.nxtgen.api.repository.NavMenuRepository;
import com.nxtgen.api.repository.UserRoleRepository;
import com.nxtgen.api.repository.UserMasterRepository;
import com.nxtgen.api.security.JwtTokenProvider;

import io.jsonwebtoken.JwtException;

import static com.nxtgen.api.constant.NxtGenCommonConstant.CHILD_TYPE;
import static com.nxtgen.api.constant.NxtGenCommonConstant.ENABLED_FLAG;
import static com.nxtgen.api.constant.NxtGenCommonConstant.NAV_MENU_ACCESS_ALL;

@Service
public class NavMenuService {

    private static final String BEARER_PREFIX = "Bearer ";

    private final NavMenuRepository navMenuRepository;
    private final UserRoleRepository userRoleRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserMasterRepository userMasterRepository;

    public NavMenuService(
            NavMenuRepository navMenuRepository,
            UserRoleRepository userRoleRepository,
            JwtTokenProvider jwtTokenProvider,
            UserMasterRepository userMasterRepository
    ) {
        this.navMenuRepository = navMenuRepository;
        this.userRoleRepository = userRoleRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userMasterRepository = userMasterRepository;
    }

    public List<NavMenuResponse> getNavigationMenu(String authorizationHeader) {
        Set<String> userRoles = resolveRoles(authorizationHeader);

        List<NavMenuEntity> rows = navMenuRepository.findByIsEnabledOrderByIdAsc(ENABLED_FLAG).stream()
                .filter(row -> isVisibleToUser(row, userRoles))
                .collect(Collectors.toList());

        Map<Long, NavMenuResponse> nodesById = rows.stream()
                .collect(Collectors.toMap(
                        NavMenuEntity::getId,
                        this::toResponse,
                        (existing, replacement) -> existing,
                        LinkedHashMap::new
                ));

        Map<Boolean, List<NavMenuEntity>> partitionedByAttachable = rows.stream()
                .collect(Collectors.partitioningBy(row ->
                        isChild(row) && row.getParentMenuId() != null && nodesById.containsKey(row.getParentMenuId())
                ));

        partitionedByAttachable.get(true).forEach(row ->
                nodesById.get(row.getParentMenuId()).getChildren().add(nodesById.get(row.getId()))
        );

        return partitionedByAttachable.get(false).stream()
                // Excludes child rows whose parent was removed (e.g. hidden by role
                // filtering above) so they don't incorrectly surface as top-level items.
                .filter(row -> !(isChild(row) && row.getParentMenuId() != null))
                .map(row -> nodesById.get(row.getId()))
                .collect(Collectors.toList());
    }

    private Set<String> resolveRoles(String authorizationHeader) {
        return extractBearerToken(authorizationHeader)
                .flatMap(this::resolveUsername)
                .flatMap(userMasterRepository::findByUsername)
                .filter(user -> ENABLED_FLAG.equalsIgnoreCase(user.getActvFlag())
                        && !ENABLED_FLAG.equalsIgnoreCase(user.getIsLocked()))
                .map(user -> {
                    Set<String> roles = userRoleRepository.findActiveRoleNamesByUsername(user.getUsername()).stream()
                            .filter(java.util.Objects::nonNull)
                            .map(this::normalizeRole)
                            .collect(Collectors.toSet());
                    if (ENABLED_FLAG.equalsIgnoreCase(user.getIsSupAdmin())) roles.add("SUPER ADMIN");
                    return roles;
                })
                .orElseGet(Set::of);
    }

    private Optional<String> resolveUsername(String token) {
        try {
            return Optional.ofNullable(jwtTokenProvider.parseClaims(token).getSubject());
        } catch (JwtException | IllegalArgumentException exception) {
            return Optional.empty();
        }
    }

    private Optional<String> extractBearerToken(String authorizationHeader) {
        return Optional.ofNullable(authorizationHeader)
                .filter(header -> header.startsWith(BEARER_PREFIX))
                .map(header -> header.substring(BEARER_PREFIX.length()).trim())
                .filter(token -> !token.isEmpty());
    }

    private boolean isVisibleToUser(NavMenuEntity row, Set<String> userRoles) {
        String accessLevel = Optional.ofNullable(row.getAccessLevel())
                .map(this::normalizeRole)
                .orElse(NAV_MENU_ACCESS_ALL);

        if ("APPROVER".equals(accessLevel)) return userRoles.contains("APPROVER");
        return userRoles.contains("SUPER ADMIN") || NAV_MENU_ACCESS_ALL.equals(accessLevel) || userRoles.contains(accessLevel);
    }

    private String normalizeRole(String role) {
        return role.trim().toUpperCase(Locale.ROOT).replaceAll("[_\\s]+", " ");
    }

    private boolean isChild(NavMenuEntity row) {
        return row.getMenuType() != null
                && CHILD_TYPE.equalsIgnoreCase(row.getMenuType().trim().toUpperCase(Locale.ROOT));
    }

    private NavMenuResponse toResponse(NavMenuEntity row) {
        return new NavMenuResponse(
                row.getId(),
                row.getMenuCode(),
                row.getMenuName(),
                row.getMenuDisplayName(),
                row.getMenuUrl(),
                row.getMenuType()
        );
    }
}
