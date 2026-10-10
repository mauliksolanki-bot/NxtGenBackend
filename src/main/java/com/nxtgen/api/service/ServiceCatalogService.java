package com.nxtgen.api.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.nxtgen.api.dto.SvcCategoryResponse;
import com.nxtgen.api.dto.SvcSubcategoryResponse;
import com.nxtgen.api.entity.SvcCategoryEntity;
import com.nxtgen.api.entity.SvcSubcategoryEntity;
import com.nxtgen.api.exception.SvcCategoryNotFoundException;
import com.nxtgen.api.repository.SvcCategoryRepository;
import com.nxtgen.api.repository.SvcSubcategoryRepository;

@Service
public class ServiceCatalogService {

    private static final String ACTIVE_FLAG = "Y";
    private static final String CATEGORY_NOT_FOUND_MESSAGE = "Service category not found.";

    private final SvcCategoryRepository svcCategoryRepository;
    private final SvcSubcategoryRepository svcSubcategoryRepository;

    public ServiceCatalogService(SvcCategoryRepository svcCategoryRepository, SvcSubcategoryRepository svcSubcategoryRepository) {
        this.svcCategoryRepository = svcCategoryRepository;
        this.svcSubcategoryRepository = svcSubcategoryRepository;
    }

    public List<SvcCategoryResponse> getActiveCategories() {
        return svcCategoryRepository.findByIsActiveOrderByDisplayOrderAsc(ACTIVE_FLAG)
                .stream()
                .map(this::toCategoryResponse)
                .collect(Collectors.toList());
    }

    public SvcCategoryResponse getCategoryBySlug(String slug) {
        return svcCategoryRepository.findBySlug(slug)
                .map(this::toCategoryResponse)
                .orElseThrow(() -> new SvcCategoryNotFoundException(CATEGORY_NOT_FOUND_MESSAGE));
    }

    public List<SvcSubcategoryResponse> getActiveSubcategoriesByCategorySlug(String categorySlug) {
        SvcCategoryEntity category = svcCategoryRepository.findBySlug(categorySlug)
                .orElseThrow(() -> new SvcCategoryNotFoundException(CATEGORY_NOT_FOUND_MESSAGE));

        return svcSubcategoryRepository.findByCategoryIdAndIsActiveOrderByDisplayOrderAsc(category.getId(), ACTIVE_FLAG)
                .stream()
                .map(this::toSubcategoryResponse)
                .collect(Collectors.toList());
    }

    private SvcCategoryResponse toCategoryResponse(SvcCategoryEntity category) {
        return new SvcCategoryResponse(category.getId(), category.getName(), category.getIcon(), category.getDescription(), category.getSlug());
    }

    private SvcSubcategoryResponse toSubcategoryResponse(SvcSubcategoryEntity subcategory) {
        return new SvcSubcategoryResponse(
                subcategory.getId(),
                subcategory.getCategoryId(),
                subcategory.getName(),
                subcategory.getIcon(),
                subcategory.getDescription(),
                subcategory.getSlug()
        );
    }
}
