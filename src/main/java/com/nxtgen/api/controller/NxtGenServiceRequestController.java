package com.nxtgen.api.controller;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.nxtgen.api.dto.SrAttachmentDeleteRequest;
import com.nxtgen.api.dto.SrAttachmentResponse;
import com.nxtgen.api.dto.SrDetailResponse;
import com.nxtgen.api.dto.SrSaveRequest;
import com.nxtgen.api.exception.AuthenticationFailedException;
import com.nxtgen.api.exception.ServiceRequestAccessException;
import com.nxtgen.api.exception.ServiceRequestNotFoundException;
import com.nxtgen.api.exception.UserValidationException;
import com.nxtgen.api.service.ServiceRequestService;

@RestController
@RequestMapping("/api/sr")
public class NxtGenServiceRequestController {
    @GetMapping("/{srId}/approvers")
    public List<com.nxtgen.api.dto.SrApproverResponse> approvers(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @PathVariable String srId) {
        return serviceRequestService.approvers(authorization, srId);
    }

    @PostMapping("/{srId}/approvers/{approvalId}")
    public List<com.nxtgen.api.dto.SrApproverResponse> updateApprover(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @PathVariable String srId, @PathVariable Long approvalId,
            @RequestBody com.nxtgen.api.dto.SrApproverUpdateRequest request) {
        return serviceRequestService.updateApprover(authorization, srId, approvalId, request);
    }
    @GetMapping("/{srId}/activities")
    public List<com.nxtgen.api.entity.SrActivityEntity> activities(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @PathVariable String srId) {
        return serviceRequestService.activities(authorization, srId);
    }

    @PostMapping("/{srId}/activities")
    public List<com.nxtgen.api.entity.SrActivityEntity> postActivity(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @PathVariable String srId, @RequestBody com.nxtgen.api.dto.SrActivityRequest request) {
        return serviceRequestService.postActivity(authorization, srId, request);
    }

    private final ServiceRequestService serviceRequestService;

    public NxtGenServiceRequestController(ServiceRequestService serviceRequestService) {
        this.serviceRequestService = serviceRequestService;
    }

    @GetMapping("/new")
    public SrDetailResponse newRequest(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestParam String categorySlug,
            @RequestParam String subcategorySlug
    ) {
        return serviceRequestService.preview(authorization, categorySlug, subcategorySlug);
    }

    @GetMapping("/users/search")
    public List<Map<String, String>> searchUsers(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestParam String query
    ) {
        return serviceRequestService.searchRequestedFor(authorization, query);
    }

    @GetMapping("/{srId}")
    public SrDetailResponse get(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @PathVariable String srId
    ) {
        return serviceRequestService.get(authorization, srId);
    }

    @PostMapping("/{srId}/update")
    public SrDetailResponse update(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @PathVariable String srId,
            @RequestBody SrSaveRequest request
    ) {
        return serviceRequestService.update(authorization, srId, request);
    }

    @PostMapping("/submit")
    public SrDetailResponse submit(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestBody SrSaveRequest request
    ) {
        return serviceRequestService.submit(authorization, request);
    }

    @PostMapping("/{srId}/delete")
    public ResponseEntity<Map<String, String>> delete(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @PathVariable String srId
    ) {
        serviceRequestService.delete(authorization, srId);
        return ResponseEntity.ok(Map.of("message", "Service request deleted successfully."));
    }

    @GetMapping("/{srId}/attachments")
    public List<SrAttachmentResponse> listAttachments(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @PathVariable String srId
    ) {
        return serviceRequestService.listAttachments(authorization, srId);
    }

    @PostMapping(value = "/{srId}/attachments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<SrAttachmentResponse> addAttachment(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @PathVariable String srId,
            @RequestParam("file") MultipartFile file
    ) {
        return serviceRequestService.addAttachment(authorization, srId, file);
    }

    @PostMapping("/{srId}/attachments/delete")
    public List<SrAttachmentResponse> deleteAttachments(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @PathVariable String srId,
            @RequestBody SrAttachmentDeleteRequest request
    ) {
        return serviceRequestService.deleteAttachments(authorization, srId, request.ids());
    }

    @PostMapping("/{srId}/attachments/{attachmentId}/rename")
    public List<SrAttachmentResponse> renameAttachment(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @PathVariable String srId,
            @PathVariable Long attachmentId,
            @RequestBody com.nxtgen.api.dto.SrAttachmentRenameRequest request
    ) {
        return serviceRequestService.renameAttachment(authorization, srId, attachmentId, request.fileName());
    }

    @GetMapping("/{srId}/attachments/{attachmentId}/file")
    public ResponseEntity<byte[]> download(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @PathVariable String srId,
            @PathVariable Long attachmentId
    ) {
        ServiceRequestService.FileDownload file = serviceRequestService.download(authorization, srId, attachmentId);
        ContentDisposition disposition = (file.inline() ? ContentDisposition.inline() : ContentDisposition.attachment())
                .filename(file.fileName(), StandardCharsets.UTF_8)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .header("X-Content-Type-Options", "nosniff")
                .contentType(MediaType.parseMediaType(file.contentType()))
                .body(file.data());
    }

    @ExceptionHandler(UserValidationException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(UserValidationException exception) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", exception.getMessage());
        body.put("errors", exception.getErrors());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<Map<String, String>> handleAuthentication(AuthenticationFailedException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", exception.getMessage()));
    }

    @ExceptionHandler(ServiceRequestNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(ServiceRequestNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", exception.getMessage()));
    }

    @ExceptionHandler(ServiceRequestAccessException.class)
    public ResponseEntity<Map<String, String>> handleAccess(ServiceRequestAccessException exception) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", exception.getMessage()));
    }
}
