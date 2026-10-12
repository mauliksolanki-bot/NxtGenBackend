package com.nxtgen.api.service;

import org.junit.jupiter.api.Test;
import com.nxtgen.api.entity.UserMasterEntity;
import com.nxtgen.api.entity.SrApprovalEntity;
import com.nxtgen.api.repository.UserRoleRepository;
import com.nxtgen.api.repository.SrApprovalRepository;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class MyApprovalsTest {
    private final AuthenticatedUserResolver resolver = mock(AuthenticatedUserResolver.class);
    private final UserRoleRepository roles = mock(UserRoleRepository.class);
    private final SrApprovalRepository approvals = mock(SrApprovalRepository.class);
    private final MyApprovalsService service = new MyApprovalsService(resolver, roles, approvals);

    @Test void rejectsSuperAdminWithoutApprover() {
        var user = mock(UserMasterEntity.class);
        when(user.getUsername()).thenReturn("admin");
        when(resolver.resolve("token")).thenReturn(user);
        when(roles.findActiveRoleNamesByUsername("admin")).thenReturn(List.of("SUPER ADMIN"));
        var error = assertThrows(org.springframework.web.server.ResponseStatusException.class, () -> service.list("token"));
        assertEquals(403, error.getStatusCode().value());
        verifyNoInteractions(approvals);
    }
    @Test void includesOnlyMembershipGroupsForApprover() {
        var user = mock(UserMasterEntity.class);
        when(user.getUsername()).thenReturn("member"); when(user.getId()).thenReturn(12L);
        when(resolver.resolve("token")).thenReturn(user);
        when(roles.findActiveRoleNamesByUsername("member")).thenReturn(List.of("APPROVER"));
        when(roles.findActiveGroupIdsByUsername("member")).thenReturn(List.of(20L));
        when(approvals.findByApproverGroupIdInOrderByCreatedAtDescIdDesc(List.of(20L)))
                .thenReturn(List.of(new SrApprovalEntity("RITM2", "REQ2", 2, "Pending", "Software Licence Management")));
        assertEquals("RITM2", service.list("token").get(0).number());
        verify(approvals).findByApproverGroupIdInOrderByCreatedAtDescIdDesc(List.of(20L));
    }

    @Test void scopesListToUserAndTheirActiveRoles() {
        var user = mock(UserMasterEntity.class);
        when(user.getUsername()).thenReturn("licence"); when(user.getId()).thenReturn(10L);
        when(resolver.resolve("token")).thenReturn(user);
        when(roles.findActiveRoleNamesByUsername("licence")).thenReturn(List.of(" approver ", "Software Licence Manager"));
        when(approvals.findAssignedApprovals(eq(10L), anyCollection())).thenReturn(List.of(new SrApprovalEntity("RITM1", "REQ1", 2, "Pending", "Software Licence Manager")));
        assertEquals("RITM1", service.list("token").get(0).number());
        verify(approvals).findAssignedApprovals(eq(10L), argThat(names -> names.contains("APPROVER") && names.contains("SOFTWARE LICENCE MANAGER")));
    }
}
