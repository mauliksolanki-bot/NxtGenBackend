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

    public NavMenuService(
            NavMenuRepository navMenuRepository,
            UserRoleRepository userRoleRepository,
            JwtTokenProvider jwtTokenProvider
    ) {
        this.navMenuRepository = navMenuRepository;
        this.userRoleRepository = userRoleRepository;
        this.jwtTokenProvider = jwtTokenProvider;
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
                .map(userRoleRepository::findActiveRoleNamesByUsername)
                .map(roleNames -> roleNames.stream()
                        .map(roleName -> roleName.trim().toUpperCase(Locale.ROOT))
                        .collect(Collectors.toSet()))
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
                .map(value -> value.trim().toUpperCase(Locale.ROOT))
                .orElse(NAV_MENU_ACCESS_ALL);

        return NAV_MENU_ACCESS_ALL.equals(accessLevel) || userRoles.contains(accessLevel);
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
