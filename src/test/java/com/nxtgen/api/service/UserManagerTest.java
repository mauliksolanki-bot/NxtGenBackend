package com.nxtgen.api.service;

import com.nxtgen.api.dto.CreateUserRequest;
import com.nxtgen.api.dto.UpdateUserRequest;
import com.nxtgen.api.entity.RoleEntity;
import com.nxtgen.api.entity.UserMasterEntity;
import com.nxtgen.api.exception.UserValidationException;
import com.nxtgen.api.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserManagerTest {
    private final UserMasterRepository users = mock(UserMasterRepository.class);
    private final RoleRepository roles = mock(RoleRepository.class);
    private final GroupRepository groups = mock(GroupRepository.class);
    private final PasswordEncoder encoder = mock(PasswordEncoder.class);
    private final UserRoleRepository userRoles = mock(UserRoleRepository.class);
    private final UserGroupRepository userGroups = mock(UserGroupRepository.class);
    private final UserManagementService service = new UserManagementService(users, userRoles, roles, userGroups, groups, encoder);
    private final UserMasterEntity user = new UserMasterEntity("First", "Last", "First Last", "user", "hash", "user@example.com", "Y", "N", "N", null);
    private final UserMasterEntity manager = new UserMasterEntity("Team", "Lead", "Team Lead", "manager", "hash", "manager@example.com", "Y", "N", "N", null);

    @BeforeEach void setup() {
        ReflectionTestUtils.setField(user, "id", 1L);
        ReflectionTestUtils.setField(manager, "id", 2L);
        when(users.findById(1L)).thenReturn(Optional.of(user));
        when(users.findById(2L)).thenReturn(Optional.of(manager));
        when(roles.findByIdAndIsActive(1L, "Y")).thenReturn(Optional.of(new RoleEntity("ITIL", "Role", "Y")));
        when(groups.existsByIdAndIsActive(1L, "Y")).thenReturn(true);
        when(users.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(encoder.encode(any())).thenReturn("hash");
    }

    @Test void createAndUpdateRequireManager() {
        CreateUserRequest create = createRequest();
        create.setManagerId(null);
        assertTrue(assertThrows(UserValidationException.class, () -> service.createUser(create)).getErrors().containsKey("manager"));
        UpdateUserRequest update = updateRequest();
        update.setManagerId(null);
        assertTrue(assertThrows(UserValidationException.class, () -> service.updateUser(update)).getErrors().containsKey("manager"));
    }
    @Test void rejectsUnknownInactiveAndSelfManagers() {
        UpdateUserRequest request = updateRequest();
        request.setManagerId(1L);
        assertTrue(assertThrows(UserValidationException.class, () -> service.updateUser(request)).getErrors().containsKey("manager"));
        request.setManagerId(99L);
        assertTrue(assertThrows(UserValidationException.class, () -> service.updateUser(request)).getErrors().containsKey("manager"));
        manager.setActvFlag("N");
        request.setManagerId(2L);
        assertTrue(assertThrows(UserValidationException.class, () -> service.updateUser(request)).getErrors().containsKey("manager"));
        verify(users, never()).save(any());
    }
    @Test void createPersistsManager() {
        service.createUser(createRequest());
        verify(users).save(argThat(saved -> Long.valueOf(2L).equals(saved.getManagerId())));
    }
    @Test void updatePersistsAndReturnsManager() {
        service.updateUser(updateRequest());
        assertEquals(2L, user.getManagerId());
        var detail = service.getUserById(1L);
        assertEquals(2L, detail.getManagerId());
        assertEquals("Team Lead (manager)", detail.getManagerName());
    }
    @Test void updateFlushesOldAssignmentsBeforeInsertingUniqueReplacements() {
        UpdateUserRequest request = updateRequest();
        request.setRoleIds(List.of(1L, 1L));
        request.setGroupIds(List.of(1L, 1L));
        service.updateUser(request);
        var order = inOrder(userRoles, userGroups);
        order.verify(userRoles).deleteByUserId(1L);
        order.verify(userRoles).flush();
        order.verify(userRoles).saveAll(argThat(items -> java.util.stream.StreamSupport.stream(items.spliterator(), false).count() == 1));
        order.verify(userGroups).deleteByUserId(1L);
        order.verify(userGroups).flush();
        order.verify(userGroups).saveAll(argThat(items -> java.util.stream.StreamSupport.stream(items.spliterator(), false).count() == 1));
    }

    @Test void createDoesNotInsertDuplicateAssignments() {
        CreateUserRequest request = createRequest();
        request.setRoleIds(List.of(1L, 1L));
        request.setGroupIds(List.of(1L, 1L));
        service.createUser(request);
        verify(userRoles).saveAll(argThat(items -> java.util.stream.StreamSupport.stream(items.spliterator(), false).count() == 1));
        verify(userGroups).saveAll(argThat(items -> java.util.stream.StreamSupport.stream(items.spliterator(), false).count() == 1));
    }

    @Test void searchExcludesCurrentUser() {
        when(users.searchActiveUsersByIdNameOrUsername("Lead")).thenReturn(List.of(user, manager));
        var options = service.searchManagers(" Lead ", 1L);
        assertEquals(1, options.size());
        assertEquals("2", options.get(0).getValue());
    }
    @Test void assignedManagerCannotBeDeleted() {
        when(users.existsByManagerId(2L)).thenReturn(true);
        assertThrows(UserValidationException.class, () -> service.deleteUser(2L));
        verify(users, never()).delete(any());
    }
    private CreateUserRequest createRequest() {
        CreateUserRequest request = new CreateUserRequest();
        request.setFirstName("New"); request.setLastName("User"); request.setEmailAddress("new@example.com");
        request.setManagerId(2L); request.setRoleIds(List.of(1L)); request.setGroupIds(List.of(1L));
        request.setIsSuperAdmin("N"); request.setIsActive("Y"); request.setPasswordResetRequired("Y");
        return request;
    }
    private UpdateUserRequest updateRequest() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setId(1L); request.setFirstName("First"); request.setLastName("Last"); request.setEmailAddress("user@example.com");
        request.setManagerId(2L); request.setRoleIds(List.of(1L)); request.setGroupIds(List.of(1L));
        request.setIsSuperAdmin("N"); request.setIsActive("Y"); request.setPasswordResetRequired("N");
        return request;
    }
}
