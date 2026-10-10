package com.nxtgen.api.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nxtgen.api.dto.CreateGroupRequest;
import com.nxtgen.api.dto.CreateGroupResponse;
import com.nxtgen.api.dto.GroupDetailResponse;
import com.nxtgen.api.dto.OptionResponse;
import com.nxtgen.api.dto.UpdateGroupRequest;
import com.nxtgen.api.exception.GroupNotFoundException;
import com.nxtgen.api.exception.GroupValidationException;
import com.nxtgen.api.service.GroupManagementService;

/**
 * Create Group workflow endpoint backed by the NXTGEN_GROUPS_ND_TEAMS table,
 * metadata for the form itself is served via the shared /api/formconfig endpoint.
 */
@RestController
@RequestMapping("/api")
public class NxtGenGroupManagementController {

    private final GroupManagementService groupManagementService;

    public NxtGenGroupManagementController(GroupManagementService groupManagementService) {
        this.groupManagementService = groupManagementService;
    }

    @PostMapping("/creategroup")
    public CreateGroupResponse createGroup(@Valid @RequestBody CreateGroupRequest request) {
        return groupManagementService.createGroup(request);
    }

    @GetMapping("/searchgroupowners")
    public List<OptionResponse> searchGroupOwners(@RequestParam String query) {
        return groupManagementService.searchGroupOwners(query);
    }

    @GetMapping("/searchgroups")
    public List<OptionResponse> searchGroups(@RequestParam String query) {
        return groupManagementService.searchGroups(query);
    }

    @GetMapping("/getgroupbyid")
    public GroupDetailResponse getGroupById(@RequestParam Long id) {
        return groupManagementService.getGroupById(id);
    }

    @PostMapping("/updategroup")
    public CreateGroupResponse updateGroup(@Valid @RequestBody UpdateGroupRequest request) {
        return groupManagementService.updateGroup(request);
    }

    @ExceptionHandler(GroupNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleGroupNotFound(GroupNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", exception.getMessage()));
    }

    @ExceptionHandler(GroupValidationException.class)
    public ResponseEntity<Map<String, Object>> handleGroupValidation(GroupValidationException exception) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", exception.getMessage());
        body.put("errors", exception.getErrors());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
}
