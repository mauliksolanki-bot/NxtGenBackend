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
    private final com.nxtgen.api.repository.SrActivityRepository activityRepository;
    private final SrMasterRepository masterRepository;
    private final SrCommentRepository commentRepository;
    private final SrApprovalRepository approvalRepository;
    private final SrApprovalCheckRepository approvalCheckRepository;
    private final SrAttachmentRepository attachmentRepository;
    private final SrSequenceRepository sequenceRepository;
    private final SvcCategoryRepository categoryRepository;
    private final SvcSubcategoryRepository subcategoryRepository;
    private final UserMasterRepository userMasterRepository;
    private final com.nxtgen.api.repository.UserRoleRepository userRoleRepository;

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
            UserMasterRepository userMasterRepository,
            com.nxtgen.api.repository.SrActivityRepository activityRepository,
            com.nxtgen.api.repository.UserRoleRepository userRoleRepository
    ) {
        this.userResolver = userResolver;
        this.activityRepository = activityRepository;
        this.masterRepository = masterRepository;
        this.commentRepository = commentRepository;
        this.approvalRepository = approvalRepository;
        this.approvalCheckRepository = approvalCheckRepository;
        this.attachmentRepository = attachmentRepository;
        this.sequenceRepository = sequenceRepository;
        this.categoryRepository = categoryRepository;
        this.subcategoryRepository = subcategoryRepository;
        this.userMasterRepository = userMasterRepository;
        this.userRoleRepository = userRoleRepository;
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
    public List<Map<String, Object>> listRequests(String authorization) {
        UserMasterEntity user = userResolver.resolve(authorization);
        // Self Service always lists the caller's requests, including for super administrators.
        List<SrMasterEntity> requests = masterRepository
                .findByOpenedByUsernameOrderByOpenedAtDescSrIdDesc(user.getUsername());
        if (requests.isEmpty()) return List.of();

        Map<String, List<SrApprovalEntity>> approvals = approvalRepository
                .findBySrIdInOrderByApprovalLevelAsc(requests.stream().map(SrMasterEntity::getSrId).toList())
                .stream().collect(java.util.stream.Collectors.groupingBy(SrApprovalEntity::getSrId));
        Map<Long, SvcSubcategoryEntity> subcategories = subcategoryRepository.findAllById(requests.stream()
                .map(SrMasterEntity::getSubcategoryId).filter(java.util.Objects::nonNull).distinct().toList())
                .stream().collect(java.util.stream.Collectors.toMap(SvcSubcategoryEntity::getId, value -> value));
        Map<Long, com.nxtgen.api.entity.SvcCategoryEntity> categories = categoryRepository.findAllById(subcategories.values()
                .stream().map(SvcSubcategoryEntity::getCategoryId).filter(java.util.Objects::nonNull).distinct().toList())
                .stream().collect(java.util.stream.Collectors.toMap(com.nxtgen.api.entity.SvcCategoryEntity::getId, value -> value));

        return requests.stream().map(sr -> {
            SvcSubcategoryEntity subcategory = subcategories.get(sr.getSubcategoryId());
            com.nxtgen.api.entity.SvcCategoryEntity category = subcategory == null ? null : categories.get(subcategory.getCategoryId());
            List<SrApprovalEntity> steps = approvals.getOrDefault(sr.getSrId(), List.of());
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("id", sr.getSrId());
            row.put("number", sr.getSrId());
            row.put("catalog", category == null ? "" : nullToEmpty(category.getName()));
            row.put("item", subcategory == null ? nullToEmpty(sr.getSubcategory()) : nullToEmpty(subcategory.getName()));
            row.put("approval", approvalStatus(steps, sr));
            row.put("quantity", sr.getQuantity());
            row.put("stage", nullToEmpty(sr.getStage()));
            row.put("state", nullToEmpty(sr.getState()));
            row.put("active", !isClosed(sr));
            return row;
        }).toList();
    }

    private static String approvalStatus(List<SrApprovalEntity> steps, SrMasterEntity sr) {
        if (steps.stream().anyMatch(step -> "Rejected".equalsIgnoreCase(step.getApprovalStatus()))) return "Rejected";
        if (steps.stream().anyMatch(step -> "Cancelled".equalsIgnoreCase(step.getApprovalStatus()))) return "Cancelled";
        if (steps.isEmpty()) return "Draft".equals(sr.getState()) ? "Not requested" : "Not required";
        if (steps.stream().allMatch(step -> "Approved".equalsIgnoreCase(step.getApprovalStatus()))) return "Approved";
        return "Requested";
    }

    @Transactional(readOnly = true)
    public List<com.nxtgen.api.dto.SrApproverResponse> approvers(String authorization, String srId) {
        UserMasterEntity user = userResolver.resolve(authorization);
        SrMasterEntity sr = loadReadable(user, srId);
        return approverResponses(approvalRepository.findBySrIdOrderByApprovalLevelAscIdAsc(srId), sr, user);
    }

    private List<com.nxtgen.api.dto.SrApproverResponse> approverResponses(List<SrApprovalEntity> steps,
            SrMasterEntity sr, UserMasterEntity user) {
        return steps.stream().map(step -> new com.nxtgen.api.dto.SrApproverResponse(
                step.getId(), nullToEmpty(step.getApprovalStatus()), nullToEmpty(step.getApprovalPendingTo()),
                nullToEmpty(step.getComments()), step.getCreatedAt() == null ? "" : step.getCreatedAt().toString(),
                step.getApprovalLevel(), !isClosed(sr) && (ENABLED_FLAG.equalsIgnoreCase(user.getIsSupAdmin())
                        || ("Pending".equals(step.getApprovalStatus()) && isAssignedApprover(user, step)))
        )).toList();
    }

    @Transactional
    public List<com.nxtgen.api.dto.SrApproverResponse> updateApprover(String authorization, String srId, Long approvalId,
            com.nxtgen.api.dto.SrApproverUpdateRequest request) {
        UserMasterEntity user = userResolver.resolve(authorization);
        boolean administrator = ENABLED_FLAG.equalsIgnoreCase(user.getIsSupAdmin());
        if (!administrator && !activeRoles(user).contains("APPROVER")) throw new ServiceRequestAccessException("The APPROVER role is required.");
        // Serialize decisions for this request so later levels cannot skip pending approvals.
        SrMasterEntity sr = masterRepository.findForUpdate(srId)
                .orElseThrow(() -> new ServiceRequestNotFoundException("Service request not found."));
        assertOpen(sr);
        List<SrApprovalEntity> steps = approvalRepository.findBySrIdOrderByApprovalLevelAscIdAsc(srId);
        SrApprovalEntity step = steps.stream().filter(value -> java.util.Objects.equals(value.getId(), approvalId))
                .findFirst().orElseThrow(() -> new ServiceRequestNotFoundException("Approval not found."));
        if (!administrator && !isAssignedApprover(user, step)) throw new ServiceRequestAccessException("This approval is not assigned to you.");
        if (!administrator && (!"Pending".equals(step.getApprovalStatus()) || !java.util.Objects.equals(clean(step.getApprovalPendingTo()), clean(request.approver()))))
            throw new ServiceRequestAccessException("You can only decide your pending approval; reassignment requires an administrator.");
        String state = clean(request.state());
        String approver = clean(request.approver());
        String comments = clean(request.comments());
        Map<String, String> errors = new LinkedHashMap<>();
        requireValue(errors, "approver", approver, "Approver is required.");
        checkLength(errors, "approver", approver, 200);
        checkLength(errors, "comments", comments, 10000);
        if (!Set.of("Not Started", "Pending", "Approved", "Rejected", "Cancelled").contains(nullToEmpty(state))) {
            errors.put("state", "Select a valid approval state.");
        }
        boolean changedState = !java.util.Objects.equals(step.getApprovalStatus(), state);
        if (changedState && !"Pending".equals(step.getApprovalStatus())) {
            errors.put("state", "Only a pending approval can be decided.");
        } else if (changedState && !Set.of("Approved", "Rejected", "Cancelled").contains(nullToEmpty(state))) {
            errors.put("state", "Choose Approved, Rejected, or Cancelled.");
        }
        if (changedState && steps.stream().anyMatch(previous -> previous.getApprovalLevel() < step.getApprovalLevel()
                && !"Approved".equals(previous.getApprovalStatus()))) {
            errors.put("state", "Earlier approval levels must be approved first.");
        }
        if (!errors.isEmpty()) throw new UserValidationException(VALIDATION_MESSAGE, errors);
        boolean changed = changedState || !java.util.Objects.equals(clean(step.getApprovalPendingTo()), approver)
                || !java.util.Objects.equals(clean(step.getComments()), comments);
        if (!changed) return approverResponses(steps, sr, user);
        step.setApprovalPendingTo(approver);
        step.setComments(comments);
        step.setApprovalStatus(state);
        approvalRepository.save(step);
        if (changedState && "Approved".equals(state)) {
            steps.stream().filter(next -> "Not Started".equals(next.getApprovalStatus())).findFirst().ifPresent(next -> {
                next.setApprovalStatus("Pending");
                approvalRepository.save(next);
            });
            if (steps.stream().allMatch(value -> "Approved".equals(value.getApprovalStatus()))) {
                sr.setStage("Approved");
                sr.setState("In Progress");
            }
        } else if (changedState) {
            steps.stream().filter(next -> "Pending".equals(next.getApprovalStatus()) || "Not Started".equals(next.getApprovalStatus()))
                    .forEach(next -> { next.setApprovalStatus("Cancelled"); approvalRepository.save(next); });
            sr.setStage("Closed");
            sr.setState("Closed Cancelled");
        }
        sr.setUpdBy(user.getUsername());
        sr.setUpdDate(LocalDateTime.now());
        masterRepository.save(sr);
        recordActivity(srId, "Approval updated", user, "Level " + step.getApprovalLevel() + "\nApprover: " + approver
                + "\nState: " + state + (comments == null ? "" : "\nComments: " + comments));
        return approverResponses(steps, sr, user);
    }

    @Transactional(readOnly = true)
    public List<com.nxtgen.api.entity.SrActivityEntity> activities(String authorization, String srId) {
        loadReadable(userResolver.resolve(authorization), srId);
        return activityRepository.findBySrIdOrderByCreatedAtDescIdDesc(srId);
    }

    @Transactional
    public List<com.nxtgen.api.entity.SrActivityEntity> postActivity(String authorization, String srId,
            com.nxtgen.api.dto.SrActivityRequest request) {
        UserMasterEntity user = userResolver.resolve(authorization);
        assertOpen(loadOwned(user, srId));
        String body = clean(request.body());
        if (body == null || body.length() > 10000) {
            throw validationError("body", "Enter a note of between 1 and 10000 characters.");
        }
        recordActivity(srId, request.customerVisible() ? "Comments" : "Work notes", user, body);
        return activityRepository.findBySrIdOrderByCreatedAtDescIdDesc(srId);
    }

    private void recordActivity(String srId, String type, UserMasterEntity user, String body) {
        activityRepository.save(new com.nxtgen.api.entity.SrActivityEntity(
                srId, type, user.getEmplNm() == null ? user.getUsername() : user.getEmplNm(), body));
    }

    private Map<String, String> snapshot(SrMasterEntity sr) {
        Map<String, String> values = new LinkedHashMap<>();
        values.put("Request type", nullToEmpty(sr.getRequestType()));
        values.put("Requested for", nullToEmpty(sr.getRequestedFor()));
        values.put("Title", nullToEmpty(sr.getTitle()));
        values.put("Configuration item", nullToEmpty(sr.getCiItem()));
        values.put("Due date", sr.getDueDate() == null ? "" : sr.getDueDate().toString());
        values.put("Estimated delivery", sr.getEstimatedDelivery() == null ? "" : sr.getEstimatedDelivery().toString());
        values.put("Quantity", sr.getQuantity() == null ? "" : sr.getQuantity().toString());
        values.put("Watch list", nullToEmpty(sr.getWatchList()));
        values.put("Backordered", String.valueOf(sr.isBackordered()));
        values.put("Order Guide", nullToEmpty(sr.getOrderGuide()));
        values.put("Description", commentRepository.findFirstBySrIdOrderByIdAsc(sr.getSrId())
                .map(SrCommentEntity::getComments).orElse(""));
        return values;
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
        UserMasterEntity user = userResolver.resolve(authorization);
        SrMasterEntity sr = loadReadable(user, srId);
        return toDetail(sr, commentRepository.findFirstBySrIdOrderByIdAsc(srId),
                user.getUsername().equals(sr.getOpenedByUsername()) || ENABLED_FLAG.equalsIgnoreCase(user.getIsSupAdmin()));
    }

    @Transactional
    public SrDetailResponse update(String authorization, String srId, SrSaveRequest request) {
        UserMasterEntity user = userResolver.resolve(authorization);
        SrMasterEntity sr = loadOwned(user, srId);
        assertOpen(sr);

        Map<String, String> before = snapshot(sr);
        applyRequest(sr, request, user, false);
        String changes = snapshot(sr).entrySet().stream()
                .filter(entry -> !java.util.Objects.equals(before.get(entry.getKey()), entry.getValue()))
                .map(entry -> entry.getKey() + ": " + before.get(entry.getKey()) + " → " + entry.getValue())
                .collect(java.util.stream.Collectors.joining("\n"));
        if (!changes.isEmpty()) recordActivity(srId, "Field changes", user, changes);
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
                .findBySubcategoryAndIsActiveOrderByApprovalLevelAsc(sr.getSubcategory(), ENABLED_FLAG);
        if (checks.isEmpty()) checks = approvalCheckRepository
                .findByRequestTypeAndIsActiveOrderByApprovalLevelAsc(sr.getRequestType(), ENABLED_FLAG);
        for (int index = 0; index < checks.size(); index++) {
            approvalRepository.save(buildApproval(sr, checks.get(index), user, index == 0));
        }

        sr.setState(STATE_SUBMITTED);
        sr.setStage(checks.isEmpty() ? STAGE_FULFILLMENT : STAGE_APPROVAL);
        masterRepository.save(sr);
        recordActivity(srId, "Request created", user, "State: " + sr.getState() + "\nStage: " + sr.getStage());

        return toDetail(sr, commentRepository.findFirstBySrIdOrderByIdAsc(srId));
    }

    @Transactional
    public void delete(String authorization, String srId) {
        SrMasterEntity sr = loadOwned(authorization, srId);

        attachmentRepository.deleteBySrId(srId);
        activityRepository.deleteBySrId(srId);
        approvalRepository.deleteBySrId(srId);
        commentRepository.deleteBySrId(srId);
        masterRepository.delete(sr);
    }

    @Transactional(readOnly = true)
    public List<SrAttachmentResponse> listAttachments(String authorization, String srId) {
        loadReadable(userResolver.resolve(authorization), srId);
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
        recordActivity(srId, "Attachment uploaded", user, fileName + "\n" + data.length + " bytes");
        return listAttachments(authorization, srId);
    }

    @Transactional
    public List<SrAttachmentResponse> deleteAttachments(String authorization, String srId, List<Long> ids) {
        UserMasterEntity user = userResolver.resolve(authorization);
        assertOpen(loadOwned(user, srId));

        if (ids == null || ids.isEmpty()) {
            throw validationError("ids", "Select at least one file to delete.");
        }
        for (Long id : ids) {
            attachmentRepository.findByIdAndSrId(id, srId).ifPresent(attachment ->
                    recordActivity(srId, "Attachment deleted", user, attachment.getFileName()));
        }
        attachmentRepository.deleteByIdInAndSrId(ids, srId);
        return listAttachments(authorization, srId);
    }

    @Transactional
    public List<SrAttachmentResponse> renameAttachment(String authorization, String srId, Long attachmentId, String fileName) {
        UserMasterEntity user = userResolver.resolve(authorization);
        assertOpen(loadOwned(user, srId));
        SrAttachmentEntity attachment = attachmentRepository.findByIdAndSrId(attachmentId, srId)
                .orElseThrow(() -> new ServiceRequestNotFoundException("Attachment not found."));
        String name = fileName == null ? "" : fileName.trim();
        if (name.isEmpty() || name.length() > 255 || name.contains("/") || name.contains("\\")
                || name.chars().anyMatch(Character::isISOControl)
                || !extensionOf(name).equals(extensionOf(attachment.getFileName()))) {
            throw validationError("fileName", "Enter a valid file name and keep the original extension.");
        }
        if (!name.equals(attachment.getFileName())) {
            recordActivity(srId, "Attachment renamed", user, attachment.getFileName() + " → " + name);
            attachment.setFileName(name);
            attachmentRepository.save(attachment);
        }
        return listAttachments(authorization, srId);
    }

    @Transactional(readOnly = true)
    public FileDownload download(String authorization, String srId, Long attachmentId) {
        loadReadable(userResolver.resolve(authorization), srId);
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

    SrApprovalEntity buildApproval(SrMasterEntity sr, SrApprovalCheckEntity check, UserMasterEntity requester, boolean first) {
        SrApprovalEntity approval = new SrApprovalEntity(sr.getSrId(), sr.getReqId(), check.getApprovalLevel(),
                first ? APPROVAL_PENDING : APPROVAL_NOT_STARTED, check.getApprovalName());
        approval.setApprovalName(check.getApprovalName());
        approval.setApproverRole(check.getApproverRole());
        approval.setApproverGroupId(check.getApproverGroupId());
        if (check.getApproverGroupId() != null) approval.setApprovalPendingTo("Software Licence Management");
        if ("MANAGER".equalsIgnoreCase(check.getApproverRole())) {
            if (requester.getManagerId() == null) throw validationError("requestedFor", "Assign a Manager to your user account before submitting this request.");
            UserMasterEntity manager = userMasterRepository.findById(requester.getManagerId())
                    .filter(candidate -> ENABLED_FLAG.equalsIgnoreCase(candidate.getActvFlag()))
                    .orElseThrow(() -> validationError("requestedFor", "Your assigned Manager is inactive or unavailable. Update your user account."));
            approval.setApproverUserId(manager.getId());
            approval.setApprovalPendingTo(manager.getEmplNm() + " (" + manager.getUsername() + ")");
        }
        return approval;
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
        checkLength(errors, "watchList", clean(request.watchList()), 1000);
        checkLength(errors, "orderGuide", clean(request.orderGuide()), 200);

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
        if (request.watchList() != null) sr.setWatchList(clean(request.watchList()));
        if (request.backordered() != null) sr.setBackordered(request.backordered());
        if (request.orderGuide() != null) sr.setOrderGuide(clean(request.orderGuide()));
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
        if (workNotes != null && !workNotes.equals(comment.getWorkNotes())) {
            recordActivity(sr.getSrId(), "Work notes", user, workNotes);
            comment.setWorkNotes(workNotes);
        }
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

    private Set<String> activeRoles(UserMasterEntity user) {
        return userRoleRepository.findActiveRoleNamesByUsername(user.getUsername()).stream()
                .filter(java.util.Objects::nonNull).map(role -> role.trim().toUpperCase(java.util.Locale.ROOT)).collect(java.util.stream.Collectors.toSet());
    }

    private boolean isAssignedApprover(UserMasterEntity user, SrApprovalEntity step) {
        Set<String> roles = activeRoles(user);
        if (!roles.contains("APPROVER")) return false;
        if (step.getApproverGroupId() != null) return userRoleRepository.findActiveGroupIdsByUsername(user.getUsername()).contains(step.getApproverGroupId());
        return step.getApproverUserId() != null ? step.getApproverUserId().equals(user.getId())
                : step.getApproverRole() != null && !"MANAGER".equalsIgnoreCase(step.getApproverRole().trim())
                        && roles.contains(step.getApproverRole().trim().toUpperCase(java.util.Locale.ROOT));
    }

    private SrMasterEntity loadReadable(UserMasterEntity user, String srId) {
        SrMasterEntity sr = masterRepository.findById(srId).orElseThrow(() -> new ServiceRequestNotFoundException("Service request not found."));
        if (!user.getUsername().equals(sr.getOpenedByUsername()) && !ENABLED_FLAG.equalsIgnoreCase(user.getIsSupAdmin())
                && (!activeRoles(user).contains("APPROVER") || approvalRepository.findBySrIdOrderByApprovalLevelAscIdAsc(srId).stream().noneMatch(step -> isAssignedApprover(user, step)))) {
            throw new ServiceRequestAccessException("This request is not assigned to you for approval.");
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
        return toDetail(sr, comment, true);
    }

    private SrDetailResponse toDetail(SrMasterEntity sr, Optional<SrCommentEntity> comment, boolean allowChanges) {
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
                allowChanges && !isClosed(sr),
                nullToEmpty(sr.getSubcategory()),
                formNameFor(sr),
                nullToEmpty(sr.getWatchList()),
                sr.isBackordered(),
                nullToEmpty(sr.getOrderGuide())
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
