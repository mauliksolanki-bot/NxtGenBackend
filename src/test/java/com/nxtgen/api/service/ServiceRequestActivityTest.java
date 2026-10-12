package com.nxtgen.api.service;

import com.nxtgen.api.dto.SrActivityRequest;
import com.nxtgen.api.dto.SrSaveRequest;
import com.nxtgen.api.entity.*;
import com.nxtgen.api.repository.*;
import com.nxtgen.api.exception.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ServiceRequestActivityTest {
    private final AuthenticatedUserResolver resolver = mock(AuthenticatedUserResolver.class);
    private final SrMasterRepository masters = mock(SrMasterRepository.class);
    private final SrCommentRepository comments = mock(SrCommentRepository.class);
    private final SrActivityRepository activities = mock(SrActivityRepository.class);
    private final SrAttachmentRepository attachments = mock(SrAttachmentRepository.class);
    private final SrMasterEntity sr = new SrMasterEntity();
    private final UserMasterEntity user = mock(UserMasterEntity.class);
    private ServiceRequestService service;

    @BeforeEach void setup() {
        when(user.getUsername()).thenReturn("owner");
        when(user.getEmplNm()).thenReturn("Owner Name");
        sr.setSrId("RITM1");
        sr.setReqId("REQ1");
        sr.setOpenedByUsername("owner");
        sr.setState("Submitted");
        when(resolver.resolve("token")).thenReturn(user);
        when(masters.findById("RITM1")).thenReturn(Optional.of(sr));
        service = new ServiceRequestService(resolver, masters, comments, mock(SrApprovalRepository.class),
                mock(SrApprovalCheckRepository.class), attachments,
                mock(SrSequenceRepository.class), mock(SvcCategoryRepository.class),
                mock(SvcSubcategoryRepository.class), mock(UserMasterRepository.class), activities, mock(UserRoleRepository.class));
    }

    @Test void renamesAttachmentAndRecordsHistory() {
        SrAttachmentEntity attachment = new SrAttachmentEntity("RITM1", "old.csv", "text/csv", 1L, new byte[]{1}, "owner", java.time.LocalDateTime.now());
        when(attachments.findByIdAndSrId(1L, "RITM1")).thenReturn(Optional.of(attachment));
        service.renameAttachment("token", "RITM1", 1L, "new.csv");
        assertEquals("new.csv", attachment.getFileName());
        verify(attachments).save(attachment);
        verify(activities).save(any(SrActivityEntity.class));
    }

    @Test void renameRejectsPathsAndExtensionChanges() {
        SrAttachmentEntity attachment = new SrAttachmentEntity("RITM1", "old.csv", "text/csv", 1L, new byte[]{1}, "owner", java.time.LocalDateTime.now());
        when(attachments.findByIdAndSrId(1L, "RITM1")).thenReturn(Optional.of(attachment));
        for (String name : java.util.List.of("", "../new.csv", "new.exe", "bad\n.csv", "a".repeat(256) + ".csv")) {
            assertThrows(UserValidationException.class, () -> service.renameAttachment("token", "RITM1", 1L, name));
        }
        verify(attachments, never()).save(any());
    }

    @Test void renameRejectsUnownedAndClosedRequests() {
        sr.setOpenedByUsername("another-user");
        assertThrows(ServiceRequestAccessException.class, () -> service.renameAttachment("token", "RITM1", 1L, "new.csv"));
        sr.setOpenedByUsername("owner");
        sr.setState("Closed Complete");
        assertThrows(UserValidationException.class, () -> service.renameAttachment("token", "RITM1", 1L, "new.csv"));
        verifyNoInteractions(attachments);
    }

    @Test void postsAppendNotesWithoutOverwritingDescription() {
        service.postActivity("token", "RITM1", new SrActivityRequest(" first ", false));
        service.postActivity("token", "RITM1", new SrActivityRequest("second", true));
        ArgumentCaptor<SrActivityEntity> entries = ArgumentCaptor.forClass(SrActivityEntity.class);
        verify(activities, times(2)).save(entries.capture());
        assertEquals("first", entries.getAllValues().get(0).getBody());
        assertEquals("Work notes", entries.getAllValues().get(0).getType());
        assertEquals("Comments", entries.getAllValues().get(1).getType());
        verifyNoInteractions(comments);
    }

    @Test void rejectsEmptyAndOversizedNotes() {
        assertThrows(UserValidationException.class, () -> service.postActivity("token", "RITM1", new SrActivityRequest("  ", false)));
        assertThrows(UserValidationException.class, () -> service.postActivity("token", "RITM1", new SrActivityRequest("x".repeat(10001), false)));
        verify(activities, never()).save(any());
    }

    @Test void closedRequestsCannotReceiveNotes() {
        sr.setState("Closed Complete");
        assertThrows(UserValidationException.class, () -> service.postActivity("token", "RITM1", new SrActivityRequest("note", false)));
        verifyNoInteractions(activities);
    }

    @Test void otherUsersCannotReadOrPostHistory() {
        sr.setOpenedByUsername("another-user");
        assertThrows(ServiceRequestAccessException.class, () -> service.activities("token", "RITM1"));
        assertThrows(ServiceRequestAccessException.class, () -> service.postActivity("token", "RITM1", new SrActivityRequest("note", false)));
        verifyNoInteractions(activities);
    }

    @Test void missingFieldsPersistAndAppearInChangeHistory() {
        service.update("token", "RITM1", new SrSaveRequest(null, "Owner Name", "", "1", "", "", "Title", "Description", "", null, null, "watcher", true, "Laptop guide"));
        assertEquals("watcher", sr.getWatchList());
        assertTrue(sr.isBackordered());
        assertEquals("Laptop guide", sr.getOrderGuide());
        ArgumentCaptor<SrActivityEntity> entry = ArgumentCaptor.forClass(SrActivityEntity.class);
        verify(activities).save(entry.capture());
        assertEquals("Field changes", entry.getValue().getType());
        assertTrue(entry.getValue().getBody().contains("Backordered: false → true"));
    }
}


