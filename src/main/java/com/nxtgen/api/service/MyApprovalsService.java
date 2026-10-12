package com.nxtgen.api.service;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import com.nxtgen.api.repository.SrApprovalRepository;
import com.nxtgen.api.repository.UserRoleRepository;

@Service
public class MyApprovalsService {
    private final AuthenticatedUserResolver resolver;
    private final UserRoleRepository roles;
    private final SrApprovalRepository approvals;
    public MyApprovalsService(AuthenticatedUserResolver resolver, UserRoleRepository roles, SrApprovalRepository approvals) {
        this.resolver = resolver; this.roles = roles; this.approvals = approvals;
    }
    public record Approval(Long id, String number, String approvalName, String state, String approver, String comments, String createdAt) {}
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public List<Approval> list(String authorization) {
        var user = resolver.resolve(authorization);
        var roleNames = roles.findActiveRoleNamesByUsername(user.getUsername()).stream()
                .filter(java.util.Objects::nonNull).map(role -> role.trim().toUpperCase(Locale.ROOT)).collect(Collectors.toSet());
        if (!roleNames.contains("APPROVER")) throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.FORBIDDEN, "The APPROVER role is required.");
        var assigned = new java.util.ArrayList<>(approvals.findAssignedApprovals(user.getId(), roleNames));
        var groupIds = roles.findActiveGroupIdsByUsername(user.getUsername());
        if (!groupIds.isEmpty()) assigned.addAll(approvals.findByApproverGroupIdInOrderByCreatedAtDescIdDesc(groupIds));
        return assigned.stream().distinct().map(step -> new Approval(step.getId(),
                step.getSrId(), step.getApprovalName(), step.getApprovalStatus(), step.getApprovalPendingTo(), step.getComments(),
                step.getCreatedAt() == null ? "" : step.getCreatedAt().toString())).toList();
    }
}
