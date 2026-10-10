package com.nxtgen.api.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nxtgen.api.dto.SvcCategoryResponse;
import com.nxtgen.api.dto.SvcSubcategoryResponse;
import com.nxtgen.api.exception.SvcCategoryNotFoundException;
import com.nxtgen.api.service.ServiceCatalogService;

/**
 * Backs the Service Catalog tile pages. Category/sub-category/service-item
 * data is driven entirely by the NXTGEN_SVC_* configuration tables rather
 * than hardcoded in the frontend.
 */
@RestController
@RequestMapping("/api")
public class NxtGenServiceCatalogController {

    private final ServiceCatalogService serviceCatalogService;

    public NxtGenServiceCatalogController(ServiceCatalogService serviceCatalogService) {
        this.serviceCatalogService = serviceCatalogService;
    }

    @GetMapping("/svccategories")
    public List<SvcCategoryResponse> getCategories() {
        return serviceCatalogService.getActiveCategories();
    }

    @GetMapping("/svccategories/slug/{slug}")
    public SvcCategoryResponse getCategoryBySlug(@PathVariable("slug") String slug) {
        return serviceCatalogService.getCategoryBySlug(slug);
    }

    @GetMapping("/svcsubcategories")
    public List<SvcSubcategoryResponse> getSubcategories(@RequestParam String categorySlug) {
        return serviceCatalogService.getActiveSubcategoriesByCategorySlug(categorySlug);
    }

    @ExceptionHandler(SvcCategoryNotFoundException.class)
    public ResponseEntity<java.util.Map<String, String>> handleCategoryNotFound(SvcCategoryNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(java.util.Map.of("message", exception.getMessage()));
    }
}
