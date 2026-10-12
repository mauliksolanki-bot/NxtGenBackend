package com.nxtgen.api.service;

import com.nxtgen.api.dto.SrSaveRequest;
import com.nxtgen.api.entity.*;
import com.nxtgen.api.repository.*;
import com.nxtgen.api.exception.UserValidationException;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.List;
import java.util.Optional;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class SoftwareApprovalTest {
    @Test void softwareSubmissionUsesManagerThenLicenceRole() {
        var resolver = mock(AuthenticatedUserResolver.class);
        var checks = mock(SrApprovalCheckRepository.class);
        var approvals = mock(SrApprovalRepository.class);
        var users = mock(UserMasterRepository.class);
        var categories = mock(SvcCategoryRepository.class);
        var subcategories = mock(SvcSubcategoryRepository.class);
        var sequences = mock(SrSequenceRepository.class);
        var user = mock(UserMasterEntity.class);
        var manager = mock(UserMasterEntity.class);
        var category = new SvcCategoryEntity();
        var subcategory = new SvcSubcategoryEntity();
        ReflectionTestUtils.setField(category, "id", 1L);
        ReflectionTestUtils.setField(subcategory, "id", 2L);
        ReflectionTestUtils.setField(subcategory, "name", "Software Installation");
        when(resolver.resolve("token")).thenReturn(user);
        when(user.getUsername()).thenReturn("requester");
        when(user.getManagerId()).thenReturn(10L);
        when(manager.getId()).thenReturn(10L);
        when(manager.getActvFlag()).thenReturn("Y");
        when(manager.getEmplNm()).thenReturn("Team Manager");
        when(manager.getUsername()).thenReturn("manager");
        when(users.findById(10L)).thenReturn(Optional.of(manager));
        when(categories.findBySlug("software")).thenReturn(Optional.of(category));
        when(subcategories.findByCategoryIdAndSlug(1L, "software-installation")).thenReturn(Optional.of(subcategory));
        when(sequences.increment(anyString())).thenReturn(1);
        when(sequences.currentValue(anyString())).thenReturn(2L);
        when(checks.findDistinctRequestTypes("Y")).thenReturn(List.of("Licence Software Request"));
        var first = rule(1, "Manager Approval", "MANAGER");
        var second = rule(2, "Software Licence Manager Approval", "SOFTWARE LICENCE MANAGER");
        when(checks.findBySubcategoryAndIsActiveOrderByApprovalLevelAsc("Software Installation", "Y")).thenReturn(List.of(first, second));
        var service = new ServiceRequestService(resolver, mock(SrMasterRepository.class), mock(SrCommentRepository.class), approvals,
                checks, mock(SrAttachmentRepository.class), sequences, categories, subcategories, users, mock(SrActivityRepository.class), mock(UserRoleRepository.class));
        service.submit("token", new SrSaveRequest("Licence Software Request", "Requester", "", "1", "", "", "Install", "Install software", "", "software", "software-installation", "", false, ""));
        var saved = org.mockito.ArgumentCaptor.forClass(SrApprovalEntity.class);
        verify(approvals, times(2)).save(saved.capture());
        assertEquals("Pending", saved.getAllValues().get(0).getApprovalStatus());
        assertEquals(10L, saved.getAllValues().get(0).getApproverUserId());
        assertEquals("Team Manager (manager)", saved.getAllValues().get(0).getApprovalPendingTo());
        assertEquals("Not Started", saved.getAllValues().get(1).getApprovalStatus());
        assertEquals("SOFTWARE LICENCE MANAGER", saved.getAllValues().get(1).getApproverRole());
        verify(checks, never()).findByRequestTypeAndIsActiveOrderByApprovalLevelAsc(anyString(), anyString());
        when(user.getManagerId()).thenReturn(null);
        assertThrows(UserValidationException.class, () -> service.buildApproval(new SrMasterEntity(), first, user, true));
        when(user.getManagerId()).thenReturn(10L);
        when(manager.getActvFlag()).thenReturn("N");
        assertThrows(UserValidationException.class, () -> service.buildApproval(new SrMasterEntity(), first, user, true));
    }

    private SrApprovalCheckEntity rule(int level, String name, String role) {
        var rule = new SrApprovalCheckEntity();
        ReflectionTestUtils.setField(rule, "approvalLevel", level);
        ReflectionTestUtils.setField(rule, "approvalName", name);
        ReflectionTestUtils.setField(rule, "approverRole", role);
        return rule;
    }
}

