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

import com.nxtgen.api.exception.GridNotFoundException;
import com.nxtgen.api.service.GridDataService;

/**
 * Common endpoint for fetching grid row data. The gridName selects which
 * underlying table is queried (each grid has its own dedicated fetch method
 * in {@link GridDataService} since the backing table differs per grid).
 */
@RestController
@RequestMapping("/api")
public class NxtGenGridDataController {

    private final GridDataService gridDataService;

    public NxtGenGridDataController(GridDataService gridDataService) {
        this.gridDataService = gridDataService;
    }

    @GetMapping("/griddata")
    public List<Map<String, Object>> getGridData(@RequestParam String gridName) {
        return gridDataService.getGridData(gridName);
    }

    @ExceptionHandler(GridNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleGridNotFound(GridNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", exception.getMessage()));
    }
}
