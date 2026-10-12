package com.nxtgen.api.service;

import com.nxtgen.api.entity.*;
import com.nxtgen.api.repository.*;
import com.nxtgen.api.exception.AuthenticationFailedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RequestsGridTest {
    private final AuthenticatedUserResolver resolver = mock(AuthenticatedUserResolver.class);
    private final SrMasterRepository masters = mock(SrMasterRepository.class);
    private final SrApprovalRepository approvals = mock(SrApprovalRepository.class);
    private final SvcSubcategoryRepository subcategories = mock(SvcSubcategoryRepository.class);
    private final SvcCategoryRepository categories = mock(SvcCategoryRepository.class);
    private final UserMasterEntity user = mock(UserMasterEntity.class);
    private final SrMasterEntity sr = new SrMasterEntity();
    private ServiceRequestService service;

    @BeforeEach void setup() {
        when(user.getUsername()).thenReturn("owner");
        when(user.getIsSupAdmin()).thenReturn("Y");
        when(resolver.resolve("token")).thenReturn(user);
        sr.setSrId("RITM1"); sr.setState("Submitted"); sr.setStage("Approval"); sr.setQuantity(2);
        sr.setSubcategoryId(10L);
        when(masters.findByOpenedByUsernameOrderByOpenedAtDescSrIdDesc("owner")).thenReturn(List.of(sr));
        service = new ServiceRequestService(resolver, masters, mock(SrCommentRepository.class), approvals,
                mock(SrApprovalCheckRepository.class), mock(SrAttachmentRepository.class),
                mock(SrSequenceRepository.class), categories, subcategories, mock(UserMasterRepository.class), mock(SrActivityRepository.class), mock(UserRoleRepository.class));
    }

    @Test void selfServiceUsesCallerScopedQueryEvenForAdministrators() {
        Map<String, Object> row = service.listRequests("token").getFirst();
        verify(masters).findByOpenedByUsernameOrderByOpenedAtDescSrIdDesc("owner");
        verify(masters, never()).findAll();
        assertEquals("RITM1", row.get("id"));
        assertEquals(2, row.get("quantity"));
        assertEquals(true, row.get("active"));
        assertEquals("Not required", row.get("approval"));
    }

    @Test void listsCatalogAndItemFromExistingCategoryMetadata() {
        SvcSubcategoryEntity subcategory = mock(SvcSubcategoryEntity.class);
        when(subcategory.getId()).thenReturn(10L); when(subcategory.getCategoryId()).thenReturn(3L); when(subcategory.getName()).thenReturn("Laptop");
        SvcCategoryEntity category = mock(SvcCategoryEntity.class);
        when(category.getId()).thenReturn(3L); when(category.getName()).thenReturn("Hardware");
        when(subcategories.findAllById(List.of(10L))).thenReturn(List.of(subcategory));
        when(categories.findAllById(List.of(3L))).thenReturn(List.of(category));
        Map<String, Object> row = service.listRequests("token").getFirst();
        assertEquals("Hardware", row.get("catalog"));
        assertEquals("Laptop", row.get("item"));
    }

    @Test void approvalRequiresAllStepsAndRejectsWhenAnyStepRejects() {
        when(approvals.findBySrIdInOrderByApprovalLevelAsc(List.of("RITM1"))).thenReturn(List.of(
                new SrApprovalEntity("RITM1", "REQ1", 1, "Approved", "Manager"),
                new SrApprovalEntity("RITM1", "REQ1", 2, "Pending", "IT Manager")));
        assertEquals("Requested", service.listRequests("token").getFirst().get("approval"));
        when(approvals.findBySrIdInOrderByApprovalLevelAsc(List.of("RITM1"))).thenReturn(List.of(
                new SrApprovalEntity("RITM1", "REQ1", 1, "Approved", "Manager"),
                new SrApprovalEntity("RITM1", "REQ1", 2, "Approved", "IT Manager")));
        assertEquals("Approved", service.listRequests("token").getFirst().get("approval"));
        when(approvals.findBySrIdInOrderByApprovalLevelAsc(List.of("RITM1"))).thenReturn(List.of(
                new SrApprovalEntity("RITM1", "REQ1", 1, "Rejected", "Manager")));
        assertEquals("Rejected", service.listRequests("token").getFirst().get("approval"));
    }

    @Test void closedRequestsRemainAvailableForAllRecordsFilter() {
        sr.setState("Closed Complete"); sr.setStage("Closed");
        assertEquals(false, service.listRequests("token").getFirst().get("active"));
    }

    @Test void emptyListDoesNotQueryUnrelatedData() {
        when(masters.findByOpenedByUsernameOrderByOpenedAtDescSrIdDesc("owner")).thenReturn(List.of());
        assertTrue(service.listRequests("token").isEmpty());
        verifyNoInteractions(approvals, categories, subcategories);
    }

    @Test void missingAuthenticationCannotReadRequests() {
        when(resolver.resolve(null)).thenThrow(new AuthenticationFailedException("Invalid session"));
        assertThrows(AuthenticationFailedException.class, () -> service.listRequests(null));
        verifyNoInteractions(masters, approvals, categories, subcategories);
    }
}

