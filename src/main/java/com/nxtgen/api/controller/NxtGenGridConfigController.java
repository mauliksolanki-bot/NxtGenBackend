package com.nxtgen.api.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nxtgen.api.dto.GridConfigResponse;
import com.nxtgen.api.exception.GridNotFoundException;
import com.nxtgen.api.service.GridConfigService;

/**
 * Common endpoint for fetching grid + grid-column metadata (NXTGEN_GRID /
 * NXTGEN_GRID_COLUMN). A single gridName drives one shared response shape for
 * every grid in the application.
 */
@RestController
@RequestMapping("/api")
public class NxtGenGridConfigController {

    private final GridConfigService gridConfigService;

    public NxtGenGridConfigController(GridConfigService gridConfigService) {
        this.gridConfigService = gridConfigService;
    }

    @GetMapping("/gridconfig")
    public GridConfigResponse getGridConfig(@RequestParam String gridName) {
        return gridConfigService.getGridConfig(gridName);
    }

    @ExceptionHandler(GridNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleGridNotFound(GridNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", exception.getMessage()));
    }
}
