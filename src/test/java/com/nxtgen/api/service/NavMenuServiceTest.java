package com.nxtgen.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import com.nxtgen.api.entity.NavMenuEntity;
import com.nxtgen.api.entity.UserMasterEntity;
import com.nxtgen.api.repository.NavMenuRepository;
import com.nxtgen.api.repository.UserMasterRepository;
import com.nxtgen.api.repository.UserRoleRepository;
import com.nxtgen.api.security.JwtTokenProvider;
import io.jsonwebtoken.Claims;

class NavMenuServiceTest {
    private final NavMenuRepository menus = mock(NavMenuRepository.class);
    private final UserRoleRepository roles = mock(UserRoleRepository.class);
    private final UserMasterRepository users = mock(UserMasterRepository.class);
    private final JwtTokenProvider tokens = mock(JwtTokenProvider.class);
    private final UserMasterEntity user = mock(UserMasterEntity.class);
    private final NavMenuService service = new NavMenuService(menus, roles, tokens, users);

    @BeforeEach
    void setup() {
        Claims claims = mock(Claims.class);
        when(tokens.parseClaims("token")).thenReturn(claims);
        when(claims.getSubject()).thenReturn("admin");
        when(users.findByUsername("admin")).thenReturn(Optional.of(user));
        when(user.getUsername()).thenReturn("admin");
        when(user.getActvFlag()).thenReturn("Y");
        when(user.getIsLocked()).thenReturn("N");
        when(user.getIsSupAdmin()).thenReturn("N");
        when(roles.findActiveRoleNamesByUsername("admin")).thenReturn(List.of());
        when(menus.findByIsEnabledOrderByIdAsc("Y")).thenReturn(List.of(
                menu(1L, "Dashboard", "ALL", null),
                menu(2L, "Administration", "SUPER ADMIN", null),
                menu(3L, "Users", "SUPER ADMIN", 2L),
                menu(4L, "Requests", "ITIL", null)));
    }

    @Test void superAdminFlagShowsAllEnabledMenusAndChildren() {
        when(user.getIsSupAdmin()).thenReturn("Y");
        var result = service.getNavigationMenu("Bearer token");
        assertEquals(List.of("Dashboard", "Administration", "Requests"), result.stream().map(item -> item.getMenuName()).toList());
        assertEquals("Users", result.get(1).getChildren().get(0).getMenuName());
    }
    @Test void superAdminRoleWorksWithoutFlag() {
        when(roles.findActiveRoleNamesByUsername("admin")).thenReturn(List.of(" super_admin "));
        assertEquals(3, service.getNavigationMenu("Bearer token").size());
    }
    @Test void approverMenuRequiresApproverEvenForSuperAdmin() {
        when(menus.findByIsEnabledOrderByIdAsc("Y")).thenReturn(List.of(menu(5L, "My Approvals", "APPROVER", null)));
        when(user.getIsSupAdmin()).thenReturn("Y");
        assertEquals(0, service.getNavigationMenu("Bearer token").size());
        when(roles.findActiveRoleNamesByUsername("admin")).thenReturn(List.of("APPROVER"));
        assertEquals("My Approvals", service.getNavigationMenu("Bearer token").get(0).getMenuName());
    }

    @Test void normalUserOnlySeesAuthorizedMenus() {
        when(roles.findActiveRoleNamesByUsername("admin")).thenReturn(List.of("ITIL"));
        assertEquals(List.of("Dashboard", "Requests"), service.getNavigationMenu("Bearer token").stream().map(item -> item.getMenuName()).toList());
    }
    @Test void lockedOrInactiveAccountDoesNotGetAdminMenus() {
        when(user.getIsSupAdmin()).thenReturn("Y");
        when(user.getIsLocked()).thenReturn("Y");
        assertEquals(1, service.getNavigationMenu("Bearer token").size());
        when(user.getIsLocked()).thenReturn("N");
        when(user.getActvFlag()).thenReturn("N");
        assertEquals(1, service.getNavigationMenu("Bearer token").size());
    }
    @Test void anonymousAndUnknownAccountsOnlySeePublicMenus() {
        assertEquals(1, service.getNavigationMenu(null).size());
        when(users.findByUsername("admin")).thenReturn(Optional.empty());
        assertEquals(1, service.getNavigationMenu("Bearer token").size());
    }

    private NavMenuEntity menu(Long id, String name, String access, Long parent) {
        NavMenuEntity menu = new NavMenuEntity();
        ReflectionTestUtils.setField(menu, "id", id);
        ReflectionTestUtils.setField(menu, "menuName", name);
        ReflectionTestUtils.setField(menu, "accessLevel", access);
        ReflectionTestUtils.setField(menu, "menuType", parent == null ? "PARENT" : "CHILD");
        ReflectionTestUtils.setField(menu, "parentMenuId", parent);
        return menu;
    }
}
