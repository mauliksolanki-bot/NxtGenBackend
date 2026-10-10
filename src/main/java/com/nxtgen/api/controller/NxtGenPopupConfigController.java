package com.nxtgen.api.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nxtgen.api.dto.PopupConfigResponse;
import com.nxtgen.api.exception.PopupNotFoundException;
import com.nxtgen.api.service.PopupConfigService;

/**
 * Common endpoint for fetching popup/confirmation-dialog metadata
 * (NXTGEN_POPUP_CONFIG). A single popupName drives one shared response shape
 * for every confirmation dialog in the application.
 */
@RestController
@RequestMapping("/api")
public class NxtGenPopupConfigController {

    private final PopupConfigService popupConfigService;

    public NxtGenPopupConfigController(PopupConfigService popupConfigService) {
        this.popupConfigService = popupConfigService;
    }

    @GetMapping("/popupconfig")
    public PopupConfigResponse getPopupConfig(@RequestParam String popupName) {
        return popupConfigService.getPopupConfig(popupName);
    }

    @ExceptionHandler(PopupNotFoundException.class)
    public ResponseEntity<Map<String, String>> handlePopupNotFound(PopupNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", exception.getMessage()));
    }
}
