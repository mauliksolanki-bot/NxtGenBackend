package com.nxtgen.api.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nxtgen.api.dto.UserWhDetailResponse;
import com.nxtgen.api.exception.UserNotFoundException;
import com.nxtgen.api.service.UsersWhService;

/**
 * Powers the Create User form's "User ID" search-and-select field by
 * looking up records from the warehouse-style bulk dataset
 * (NXTGEN_USERS_MASTER_DATA_WH).
 */
@RestController
@RequestMapping("/api")
public class NxtGenUsersWHController {

    private final UsersWhService usersWhService;

    public NxtGenUsersWHController(UsersWhService usersWhService) {
        this.usersWhService = usersWhService;
    }

    @GetMapping("/searchuserdetails")
    public List<UserWhDetailResponse> searchUserDetails(@RequestParam String query) {
        return usersWhService.searchUsers(query);
    }

    @GetMapping("/fetchuserdetails")
    public UserWhDetailResponse fetchUserDetails(@RequestParam Long id) {
        return usersWhService.fetchUserDetails(id);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleUserNotFound(UserNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", exception.getMessage()));
    }
}
