package com.nxtgen.api.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.nxtgen.api.dto.SrAttachmentResponse;
import com.nxtgen.api.dto.SrDetailResponse;
import com.nxtgen.api.dto.SrSaveRequest;
import com.nxtgen.api.entity.SrApprovalCheckEntity;
import com.nxtgen.api.entity.SrApprovalEntity;
import com.nxtgen.api.entity.SrAttachmentEntity;
import com.nxtgen.api.entity.SrCommentEntity;
import com.nxtgen.api.entity.SrMasterEntity;
import com.nxtgen.api.entity.SvcSubcategoryEntity;
import com.nxtgen.api.entity.UserMasterEntity;
import com.nxtgen.api.exception.ServiceRequestAccessException;
import com.nxtgen.api.exception.ServiceRequestNotFoundException;
import com.nxtgen.api.exception.UserValidationException;
import com.nxtgen.api.repository.SrApprovalCheckRepository;
import com.nxtgen.api.repository.SrApprovalRepository;
import com.nxtgen.api.repository.SrAttachmentRepository;
import com.nxtgen.api.repository.SrCommentRepository;
import com.nxtgen.api.repository.SrMasterRepository;
import com.nxtgen.api.repository.SrSequenceRepository;
import com.nxtgen.api.repository.SvcCategoryRepository;
import com.nxtgen.api.repository.UserMasterRepository;
import com.nxtgen.api.repository.SvcSubcategoryRepository;

import static com.nxtgen.api.constant.NxtGenCommonConstant.ENABLED_FLAG;

@Service
public class ServiceRequestService {

    private static final String VALIDATION_MESSAGE = "Please correct the highlighted fields.";
    private static final String TITLE_PREFIX = "Request From: ";
    private static final String DEFAULT_FORM_NAME = "CREATE_SR_FORM";
    private static final String SR_SEQUENCE = "SR";
    private static final String REQ_SEQUENCE = "REQ";
    private static final String SR_PREFIX = "RITM";
    private static final String REQ_PREFIX = "REQ";
    private static final String STAGE_NEW = "New";
    private static final String STAGE_APPROVAL = "Approval";
    private static final String STAGE_FULFILLMENT = "Fulfillment";
    private static final String STATE_DRAFT = "Draft";
    private static final String STATE_SUBMITTED = "Submitted";
    private static final String APPROVAL_PENDING = "Pending";
    private static final String APPROVAL_NOT_STARTED = "Not Started";
    private static final int MAX_ATTACHMENTS = 20;
    private static final int MAX_FILE_NAME_LENGTH = 200;
    private static final long MAX_FILE_BYTES = 5L * 1024 * 1024;
    private static final DateTimeFormatter OPENED_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private static final Map<String, String> CONTENT_TYPES = Map.ofEntries(
            Map.entry("pdf", "application/pdf"),
            Map.entry("doc", "application/msword"),
            Map.entry("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
            Map.entry("xls", "application/vnd.ms-excel"),
            Map.entry("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
            Map.entry("ppt", "application/vnd.ms-powerpoint"),
            Map.entry("pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation"),
            Map.entry("txt", "text/plain"),
            Map.entry("csv", "text/csv"),
            Map.entry("png", "image/png"),
            Map.entry("jpg", "image/jpeg"),
            Map.entry("jpeg", "image/jpeg"),
            Map.entry("gif", "image/gif")
    );
    private static final Set<String> INLINE_EXTENSIONS = Set.of("pdf", "png", "jpg", "jpeg", "gif", "txt");

    private final AuthenticatedUserResolver userResolver;
    private final SrMasterRepository masterRepository;
    private final SrCommentRepository commentRepository;
    private final SrApprovalRepository approvalRepository;
    private final SrApprovalCheckRepository approvalCheckRepository;
    private final SrAttachmentRepository attachmentRepository;
    private final SrSequenceRepository sequenceRepository;
    private final SvcCategoryRepository categoryRepository;
    private final SvcSubcategoryRepository subcategoryRepository;
    private final UserMasterRepository userMasterRepository;

    public ServiceRequestService(
            AuthenticatedUserResolver userResolver,
            SrMasterRepository masterRepository,
            SrCommentRepository commentRepository,
            SrApprovalRepository approvalRepository,
            SrApprovalCheckRepository approvalCheckRepository,
            SrAttachmentRepository attachmentRepository,
            SrSequenceRepository sequenceRepository,
            SvcCategoryRepository categoryRepository,
            SvcSubcategoryRepository subcategoryRepository,
            UserMasterRepository userMasterRepository
    ) {
        this.userResolver = userResolver;
        this.masterRepository = masterRepository;
        this.commentRepository = commentRepository;
        this.approvalRepository = approvalRepository;
        this.approvalCheckRepository = approvalCheckRepository;
        this.attachmentRepository = attachmentRepository;
        this.sequenceRepository = sequenceRepository;
        this.categoryRepository = categoryRepository;
        this.subcategoryRepository = subcategoryRepository;
        this.userMasterRepository = userMasterRepository;
    }

    @Transactional(readOnly = true)
    public List<Map<String, String>> searchRequestedFor(String authorization, String query) {
        userResolver.resolve(authorization);
        String trimmed = query == null ? "" : query.trim();
        if (trimmed.isEmpty()) {
            return List.of();
        }
        return userMasterRepository.searchActiveUsersByIdNameOrUsername(trimmed).stream()
                .map(u -> Map.of("id", String.valueOf(u.getId()), "username", u.getUsername(), "emplNm", u.getEmplNm()))
                .toList();
    }

    public record FileDownload(byte[] data, String fileName, String contentType, boolean inline) {
    }

    @Transactional(readOnly = true)
    public SrDetailResponse preview(String authorization, String categorySlug, String subcategorySlug) {
        UserMasterEntity user = userResolver.resolve(authorization);
        SvcSubcategoryEntity subcategory = resolveSubcategory(categorySlug, subcategorySlug);

        SrMasterEntity sr = new SrMasterEntity();
        sr.setSrId(SR_PREFIX + formatNumber(sequenceRepository.currentValue(SR_SEQUENCE)));
        sr.setReqId(REQ_PREFIX + formatNumber(sequenceRepository.currentValue(REQ_SEQUENCE)));
        sr.setOpenedAt(LocalDateTime.now());
        sr.setOpenedBy(user.getEmplNm());
        sr.setStage(STAGE_NEW);
        sr.setState(STATE_DRAFT);
        sr.setQuantity(1);
        sr.setSubcategoryId(subcategory.getId());
        sr.setSubcategory(subcategory.getName());
        sr.setTitle(TITLE_PREFIX + subcategory.getName());

        return toDetail(sr, Optional.empty());
    }

    @Transactional(readOnly = true)
    public SrDetailResponse get(String authorization, String srId) {
        SrMasterEntity sr = loadOwned(authorization, srId);
        return toDetail(sr, commentRepository.findFirstBySrIdOrderByIdAsc(srId));
    }

    @Transactional
    public SrDetailResponse update(String authorization, String srId, SrSaveRequest request) {
        UserMasterEntity user = userResolver.resolve(authorization);
        SrMasterEntity sr = loadOwned(user, srId);
        assertOpen(sr);

        applyRequest(sr, request, user, false);
        return toDetail(sr, commentRepository.findFirstBySrIdOrderByIdAsc(srId));
    }

    @Transactional
    public SrDetailResponse submit(String authorization, SrSaveRequest request) {
        UserMasterEntity user = userResolver.resolve(authorization);
        LocalDateTime now = LocalDateTime.now();

        SrMasterEntity sr = new SrMasterEntity();
        sr.setOpenedAt(now);
        sr.setOpenedBy(user.getEmplNm());
        sr.setOpenedByUsername(user.getUsername());
        sr.setCreBy(user.getUsername());
        sr.setCreDate(now);

        SvcSubcategoryEntity subcategory = resolveSubcategory(request.categorySlug(), request.subcategorySlug());
        sr.setSubcategoryId(subcategory.getId());
        sr.setSubcategory(subcategory.getName());

        sr.setSrId(SR_PREFIX + formatNumber(nextSequence(SR_SEQUENCE)));
        sr.setReqId(REQ_PREFIX + formatNumber(nextSequence(REQ_SEQUENCE)));
        sr.setStage(STAGE_NEW);
        sr.setState(STATE_DRAFT);
        applyRequest(sr, request, user, true);
        String srId = sr.getSrId();

        List<SrApprovalCheckEntity> checks = approvalCheckRepository
                .findByRequestTypeAndIsActiveOrderByApprovalLevelAsc(sr.getRequestType(), ENABLED_FLAG);
        checks.forEach(check -> approvalRepository.save(new SrApprovalEntity(
                srId,
                sr.getReqId(),
                check.getApprovalLevel(),
                check == checks.get(0) ? APPROVAL_PENDING : APPROVAL_NOT_STARTED,
                check.getApprovalName()
        )));

        sr.setState(STATE_SUBMITTED);
        sr.setStage(checks.isEmpty() ? STAGE_FULFILLMENT : STAGE_APPROVAL);
        masterRepository.save(sr);

        return toDetail(sr, commentRepository.findFirstBySrIdOrderByIdAsc(srId));
    }

    @Transactional
    public void delete(String authorization, String srId) {
        SrMasterEntity sr = loadOwned(authorization, srId);

        attachmentRepository.deleteBySrId(srId);
        approvalRepository.deleteBySrId(srId);
        commentRepository.deleteBySrId(srId);
        masterRepository.delete(sr);
    }

    @Transactional(readOnly = true)
    public List<SrAttachmentResponse> listAttachments(String authorization, String srId) {
        loadOwned(authorization, srId);
        return attachmentRepository.findBySrIdOrderByIdAsc(srId).stream()
                .map(view -> new SrAttachmentResponse(view.getId(), view.getFileName(), view.getContentType(), view.getFileSize()))
                .toList();
    }

    @Transactional
    public List<SrAttachmentResponse> addAttachment(String authorization, String srId, MultipartFile file) {
        UserMasterEntity user = userResolver.resolve(authorization);
        SrMasterEntity sr = loadOwned(user, srId);
        assertOpen(sr);

        if (file == null || file.isEmpty()) {
            throw validationError("file", "Please choose a file to attach.");
        }
        if (file.getSize() > MAX_FILE_BYTES) {
            throw validationError("file", "File size must not exceed 5 MB.");
        }
        if (attachmentRepository.countBySrId(srId) >= MAX_ATTACHMENTS) {
            throw validationError("file", "A request can have at most " + MAX_ATTACHMENTS + " attachments.");
        }

        String fileName = sanitizeFileName(file.getOriginalFilename());
        String contentType = CONTENT_TYPES.get(extensionOf(fileName));
        if (contentType == null) {
            throw validationError("file", "This file type is not allowed.");
        }

        byte[] data;
        try {
            data = file.getBytes();
        } catch (IOException exception) {
            throw validationError("file", "The file could not be read.");
        }

        attachmentRepository.save(new SrAttachmentEntity(
                srId, fileName, contentType, (long) data.length, data, user.getUsername(), LocalDateTime.now()
        ));
        return listAttachments(authorization, srId);
    }

    @Transactional
    public List<SrAttachmentResponse> deleteAttachments(String authorization, String srId, List<Long> ids) {
        UserMasterEntity user = userResolver.resolve(authorization);
        assertOpen(loadOwned(user, srId));

        if (ids == null || ids.isEmpty()) {
            throw validationError("ids", "Select at least one file to delete.");
        }
        attachmentRepository.deleteByIdInAndSrId(ids, srId);
        return listAttachments(authorization, srId);
    }

    @Transactional(readOnly = true)
    public FileDownload download(String authorization, String srId, Long attachmentId) {
        loadOwned(authorization, srId);
        SrAttachmentEntity attachment = attachmentRepository.findByIdAndSrId(attachmentId, srId)
                .orElseThrow(() -> new ServiceRequestNotFoundException("Attachment not found."));

        String extension = extensionOf(attachment.getFileName());
        return new FileDownload(
                attachment.getFileData(),
                attachment.getFileName(),
                attachment.getContentType(),
                INLINE_EXTENSIONS.contains(extension)
        );
    }

    private void applyRequest(SrMasterEntity sr, SrSaveRequest request, UserMasterEntity user, boolean forSubmit) {
        Map<String, String> errors = new LinkedHashMap<>();

        String requestType = clean(request.requestType());
        String requestedFor = clean(request.requestedFor());
        String title = clean(request.title());
        String description = clean(request.description());
        String workNotes = clean(request.workNotes());
        String ciItem = clean(request.ciItem());

        if (forSubmit) {
            requireValue(errors, "requestType", requestType, "Request Type is required.");
            requireValue(errors, "requestedFor", requestedFor, "Requested For is required.");
            requireValue(errors, "title", title, "Title is required.");
            requireValue(errors, "description", description, "Description is required.");
        }
        checkLength(errors, "requestedFor", requestedFor, 200);
        checkLength(errors, "title", title, 200);
        checkLength(errors, "ciItem", ciItem, 100);
        checkLength(errors, "requestType", requestType, 100);

        if (requestType != null && approvalCheckRepository.findDistinctRequestTypes(ENABLED_FLAG).stream()
                .noneMatch(requestType::equals)) {
            errors.put("requestType", "Select a valid Request Type.");
        }

        LocalDate dueDate = parseDate(errors, "dueDate", request.dueDate());
        LocalDate estimatedDelivery = parseDate(errors, "estimatedDelivery", request.estimatedDelivery());
        Integer quantity = parseQuantity(errors, request.quantity(), forSubmit);

        if (!errors.isEmpty()) {
            throw new UserValidationException(VALIDATION_MESSAGE, errors);
        }

        sr.setRequestType(requestType);
        sr.setRequestedFor(requestedFor);
        sr.setTitle(title);
        sr.setCiItem(ciItem);
        sr.setDueDate(dueDate);
        sr.setEstimatedDelivery(estimatedDelivery);
        sr.setQuantity(quantity);
        sr.setUpdBy(user.getUsername());
        sr.setUpdDate(LocalDateTime.now());
        masterRepository.save(sr);

        SrCommentEntity comment = commentRepository.findFirstBySrIdOrderByIdAsc(sr.getSrId())
                .orElseGet(SrCommentEntity::new);
        comment.setSrId(sr.getSrId());
        comment.setReqId(sr.getReqId());
        comment.setComments(description);
        comment.setWorkNotes(workNotes);
        commentRepository.save(comment);
    }

    private SrMasterEntity loadOwned(String authorization, String srId) {
        return loadOwned(userResolver.resolve(authorization), srId);
    }

    private SrMasterEntity loadOwned(UserMasterEntity user, String srId) {
        SrMasterEntity sr = masterRepository.findById(srId == null ? "" : srId)
                .orElseThrow(() -> new ServiceRequestNotFoundException("Service request not found."));

        boolean owner = user.getUsername().equals(sr.getOpenedByUsername());
        if (!owner && !ENABLED_FLAG.equalsIgnoreCase(user.getIsSupAdmin())) {
            throw new ServiceRequestAccessException("You are not allowed to access this service request.");
        }
        return sr;
    }

    private SvcSubcategoryEntity resolveSubcategory(String categorySlug, String subcategorySlug) {
        return categoryRepository.findBySlug(categorySlug == null ? "" : categorySlug)
                .flatMap(category -> subcategoryRepository.findByCategoryIdAndSlug(category.getId(), subcategorySlug == null ? "" : subcategorySlug))
                .orElseThrow(() -> new ServiceRequestNotFoundException("Service subcategory not found."));
    }

    private String formNameFor(SrMasterEntity sr) {
        return Optional.ofNullable(sr.getSubcategoryId())
                .flatMap(subcategoryRepository::findById)
                .map(SvcSubcategoryEntity::getFormName)
                .filter(name -> name != null && !name.isBlank())
                .orElse(DEFAULT_FORM_NAME);
    }

    private static boolean isClosed(SrMasterEntity sr) {
        return sr.getState() != null && sr.getState().startsWith("Closed");
    }

    private void assertOpen(SrMasterEntity sr) {
        if (isClosed(sr)) {
            throw validationError("state", "This request is closed and cannot be changed.");
        }
    }

    private long nextSequence(String name) {
        if (sequenceRepository.increment(name) == 0) {
            throw new IllegalStateException("Sequence " + name + " is not configured.");
        }
        return sequenceRepository.currentValue(name) - 1;
    }

    private SrDetailResponse toDetail(SrMasterEntity sr, Optional<SrCommentEntity> comment) {
        return new SrDetailResponse(
                sr.getSrId(),
                sr.getReqId(),
                nullToEmpty(sr.getRequestType()),
                nullToEmpty(sr.getRequestedFor()),
                sr.getOpenedAt() == null ? "" : sr.getOpenedAt().format(OPENED_FORMAT),
                nullToEmpty(sr.getOpenedBy()),
                nullToEmpty(sr.getStage()),
                nullToEmpty(sr.getState()),
                sr.getDueDate() == null ? "" : sr.getDueDate().toString(),
                sr.getQuantity() == null ? "" : String.valueOf(sr.getQuantity()),
                nullToEmpty(sr.getCiItem()),
                sr.getEstimatedDelivery() == null ? "" : sr.getEstimatedDelivery().toString(),
                nullToEmpty(sr.getTitle()),
                comment.map(SrCommentEntity::getComments).orElse(""),
                comment.map(SrCommentEntity::getWorkNotes).orElse(""),
                !isClosed(sr),
                nullToEmpty(sr.getSubcategory()),
                formNameFor(sr)
        );
    }

    private static String formatNumber(long value) {
        return String.format("%07d", value);
    }

    private static String clean(String value) {
        return value == null || value.trim().isEmpty() ? null : value.trim();
    }

    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private static void requireValue(Map<String, String> errors, String field, String value, String message) {
        if (value == null) {
            errors.put(field, message);
        }
    }

    private static void checkLength(Map<String, String> errors, String field, String value, int max) {
        if (value != null && value.length() > max) {
            errors.putIfAbsent(field, "Must not exceed " + max + " characters.");
        }
    }

    private static LocalDate parseDate(Map<String, String> errors, String field, String raw) {
        String value = clean(raw);
        if (value == null) {
            return null;
        }
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException exception) {
            errors.put(field, "Enter a valid date.");
            return null;
        }
    }

    private static Integer parseQuantity(Map<String, String> errors, String raw, boolean required) {
        String value = clean(raw);
        if (value == null) {
            if (required) {
                errors.put("quantity", "Quantity is required.");
            }
            return null;
        }
        try {
            int parsed = Integer.parseInt(value);
            if (parsed < 1 || parsed > 100000) {
                errors.put("quantity", "Quantity must be between 1 and 100000.");
                return null;
            }
            return parsed;
        } catch (NumberFormatException exception) {
            errors.put("quantity", "Quantity must be a whole number.");
            return null;
        }
    }

    private static String sanitizeFileName(String original) {
        String name = original == null ? "" : original.replace('\\', '/');
        name = name.substring(name.lastIndexOf('/') + 1).replaceAll("[\\p{Cntrl}\"<>|:*?]", "_").trim();
        if (name.isEmpty()) {
            throw validationError("file", "The file name is not valid.");
        }
        if (name.length() > MAX_FILE_NAME_LENGTH) {
            throw validationError("file", "The file name is too long.");
        }
        return name;
    }

    private static String extensionOf(String fileName) {
        int dot = fileName.lastIndexOf('.');
        return dot < 0 ? "" : fileName.substring(dot + 1).toLowerCase(Locale.ROOT);
    }

    private static UserValidationException validationError(String field, String message) {
        return new UserValidationException(message, Map.of(field, message));
    }
}
