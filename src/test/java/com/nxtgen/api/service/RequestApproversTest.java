package com.nxtgen.api.service;

import com.nxtgen.api.dto.SrApproverUpdateRequest;
import com.nxtgen.api.entity.*;
import com.nxtgen.api.repository.*;
import com.nxtgen.api.exception.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RequestApproversTest {
    private final AuthenticatedUserResolver resolver = mock(AuthenticatedUserResolver.class);
    private final SrMasterRepository masters = mock(SrMasterRepository.class);
    private final SrApprovalRepository approvals = mock(SrApprovalRepository.class);
    private final SrActivityRepository activities = mock(SrActivityRepository.class);
    private final UserRoleRepository userRoles = mock(UserRoleRepository.class);
    private final UserMasterEntity user = mock(UserMasterEntity.class);
    private final SrMasterEntity sr = new SrMasterEntity();
    private final SrApprovalEntity first = new SrApprovalEntity("RITM1", "REQ1", 1, "Pending", "Line Manager");
    private final SrApprovalEntity second = new SrApprovalEntity("RITM1", "REQ1", 2, "Not Started", "IT Manager");
    private ServiceRequestService service;

    @BeforeEach void setup() {
        when(user.getUsername()).thenReturn("owner"); when(user.getEmplNm()).thenReturn("Owner"); when(user.getIsSupAdmin()).thenReturn("Y");
        when(resolver.resolve("token")).thenReturn(user);
        sr.setSrId("RITM1"); sr.setOpenedByUsername("owner"); sr.setState("Submitted"); sr.setStage("Approval");
        when(masters.findById("RITM1")).thenReturn(Optional.of(sr));
        when(masters.findForUpdate("RITM1")).thenReturn(Optional.of(sr));
        ReflectionTestUtils.setField(first, "id", 1L); ReflectionTestUtils.setField(second, "id", 2L);
        first.initializeCreatedAt(); second.initializeCreatedAt();
        when(approvals.findBySrIdOrderByApprovalLevelAscIdAsc("RITM1")).thenReturn(List.of(first, second));
        service = new ServiceRequestService(resolver, masters, mock(SrCommentRepository.class), approvals,
                mock(SrApprovalCheckRepository.class), mock(SrAttachmentRepository.class), mock(SrSequenceRepository.class),
                mock(SvcCategoryRepository.class), mock(SvcSubcategoryRepository.class), mock(UserMasterRepository.class), activities, userRoles);
    }

    @Test void assignedManagerCanReadAndApproveButCannotEditTicketOrOtherSteps() {
        when(user.getId()).thenReturn(10L);
        when(user.getIsSupAdmin()).thenReturn("N");
        when(userRoles.findActiveRoleNamesByUsername("owner")).thenReturn(List.of("APPROVER", "MANAGER"));
        sr.setOpenedByUsername("requester");
        first.setApproverUserId(10L);
        first.setApproverRole("MANAGER");
        assertFalse(service.get("token", "RITM1").editable());
        var rows = service.approvers("token", "RITM1");
        assertTrue(rows.get(0).editable()); assertFalse(rows.get(1).editable());
        assertThrows(ServiceRequestAccessException.class, () -> service.updateApprover("token", "RITM1", 2L, new SrApproverUpdateRequest("Approved", "IT Manager", "")));
        assertThrows(ServiceRequestAccessException.class, () -> service.updateApprover("token", "RITM1", 1L, new SrApproverUpdateRequest("Approved", "Different manager", "")));
        service.updateApprover("token", "RITM1", 1L, new SrApproverUpdateRequest("Approved", "Line Manager", "Reviewed"));
        assertEquals("Pending", second.getApprovalStatus());
        assertThrows(ServiceRequestAccessException.class, () -> service.delete("token", "RITM1"));
    }

    @Test void unrelatedManagerCannotReadAssignedRequest() {
        when(user.getId()).thenReturn(11L); when(user.getIsSupAdmin()).thenReturn("N");
        when(userRoles.findActiveRoleNamesByUsername("owner")).thenReturn(List.of("APPROVER", "MANAGER"));
        sr.setOpenedByUsername("requester"); first.setApproverUserId(10L); first.setApproverRole("MANAGER");
        assertThrows(ServiceRequestAccessException.class, () -> service.get("token", "RITM1"));
    }

    @Test void groupMemberWithApproverRoleCanDecideLicenceApproval() {
        when(user.getIsSupAdmin()).thenReturn("N");
        when(userRoles.findActiveRoleNamesByUsername("owner")).thenReturn(List.of("APPROVER"));
        when(userRoles.findActiveGroupIdsByUsername("owner")).thenReturn(List.of(20L));
        second.setApproverGroupId(20L); first.setApprovalStatus("Approved"); second.setApprovalStatus("Pending");
        service.updateApprover("token", "RITM1", 2L, new SrApproverUpdateRequest("Approved", "IT Manager", "Reviewed"));
        assertEquals("Approved", second.getApprovalStatus());
    }

    @Test void licenceRoleAloneCannotReplaceGroupMembership() {
        when(user.getIsSupAdmin()).thenReturn("N");
        when(userRoles.findActiveRoleNamesByUsername("owner")).thenReturn(List.of("APPROVER", "SOFTWARE LICENCE MANAGER"));
        second.setApproverGroupId(20L); first.setApprovalStatus("Approved"); second.setApprovalStatus("Pending");
        assertThrows(ServiceRequestAccessException.class, () -> service.updateApprover("token", "RITM1", 2L, new SrApproverUpdateRequest("Approved", "IT Manager", "")));
    }

    @Test void groupMemberWithoutApproverRoleCannotDecide() {
        when(user.getIsSupAdmin()).thenReturn("N");
        when(userRoles.findActiveGroupIdsByUsername("owner")).thenReturn(List.of(20L));
        second.setApproverGroupId(20L); second.setApprovalStatus("Pending");
        assertThrows(ServiceRequestAccessException.class, () -> service.updateApprover("token", "RITM1", 2L, new SrApproverUpdateRequest("Approved", "IT Manager", "")));
    }

    @Test void ownerCanReadButCannotEditApprovals() {
        when(user.getIsSupAdmin()).thenReturn("N");
        var result = service.approvers("token", "RITM1");
        assertEquals(2, result.size()); assertFalse(result.getFirst().editable());
        assertEquals("Pending", result.getFirst().state()); assertFalse(result.getFirst().createdAt().isEmpty());
        assertThrows(ServiceRequestAccessException.class, () -> service.updateApprover("token", "RITM1", 1L, new SrApproverUpdateRequest("Approved", "Manager", "Approved")));
        verify(masters, never()).findForUpdate(any());
    }

    @Test void unrelatedUserCannotReadApprovers() {
        when(user.getIsSupAdmin()).thenReturn("N"); sr.setOpenedByUsername("another-owner");
        assertThrows(ServiceRequestAccessException.class, () -> service.approvers("token", "RITM1"));
        verifyNoInteractions(approvals);
    }

    @Test void approvingPendingStepActivatesNextLevelAndLogsHistory() {
        var result = service.updateApprover("token", "RITM1", 1L, new SrApproverUpdateRequest("Approved", "Manager", "Reviewed"));
        assertEquals("Approved", first.getApprovalStatus()); assertEquals("Pending", second.getApprovalStatus());
        assertEquals("Reviewed", result.getFirst().comments()); assertEquals("Manager", result.getFirst().approver());
        assertEquals("Approval", sr.getStage()); verify(activities).save(any()); verify(masters).findForUpdate("RITM1");
    }

    @Test void finalApprovalSetsRequestStageToApproved() {
        first.setApprovalStatus("Approved"); second.setApprovalStatus("Pending");
        service.updateApprover("token", "RITM1", 2L, new SrApproverUpdateRequest("Approved", "IT Manager", ""));
        assertEquals("Approved", sr.getStage()); assertEquals("In Progress", sr.getState());
    }

    @Test void rejectionClosesRequestAndCancelsRemainingApprovals() {
        var result = service.updateApprover("token", "RITM1", 1L, new SrApproverUpdateRequest("Rejected", "Line Manager", "Not permitted"));
        assertEquals("Rejected", first.getApprovalStatus()); assertEquals("Cancelled", second.getApprovalStatus());
        assertEquals("Closed Cancelled", sr.getState()); assertTrue(result.stream().noneMatch(value -> value.editable()));
    }

    @Test void laterLevelsAndFinalDecisionsCannotBeOverridden() {
        assertThrows(UserValidationException.class, () -> service.updateApprover("token", "RITM1", 2L, new SrApproverUpdateRequest("Approved", "IT Manager", "")));
        first.setApprovalStatus("Approved");
        assertThrows(UserValidationException.class, () -> service.updateApprover("token", "RITM1", 1L, new SrApproverUpdateRequest("Pending", "Manager", "")));
        verify(approvals, never()).save(any());
    }

    @Test void rejectsInvalidInputAndApprovalsOutsideRequest() {
        assertThrows(UserValidationException.class, () -> service.updateApprover("token", "RITM1", 1L, new SrApproverUpdateRequest("Pending", "", "x".repeat(10001))));
        assertThrows(ServiceRequestNotFoundException.class, () -> service.updateApprover("token", "RITM1", 99L, new SrApproverUpdateRequest("Approved", "Manager", "")));
        assertEquals("Pending", first.getApprovalStatus()); verify(approvals, never()).save(any());
    }

    @Test void closedRequestsRemainReadableButCannotBeEdited() {
        sr.setState("Closed Complete");
        assertTrue(service.approvers("token", "RITM1").stream().noneMatch(value -> value.editable()));
        assertThrows(UserValidationException.class, () -> service.updateApprover("token", "RITM1", 1L, new SrApproverUpdateRequest("Approved", "Manager", "")));
        verify(approvals, never()).save(any());
    }
}

