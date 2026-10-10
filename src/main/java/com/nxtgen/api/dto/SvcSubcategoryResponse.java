package com.nxtgen.api.dto;

/**
 * Powers the Service Catalog sub-category tile page (the second step of the
 * Create Service Request wizard).
 */
public class SvcSubcategoryResponse {

    private final Long id;
    private final Long categoryId;
    private final String name;
    private final String icon;
    private final String description;
    private final String slug;

    public SvcSubcategoryResponse(Long id, Long categoryId, String name, String icon, String description, String slug) {
        this.id = id;
        this.categoryId = categoryId;
        this.name = name;
        this.icon = icon;
        this.description = description;
        this.slug = slug;
    }

    public Long getId() {
        return id;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public String getName() {
        return name;
    }

    public String getIcon() {
        return icon;
    }

    public String getDescription() {
        return description;
    }

    public String getSlug() {
        return slug;
    }
}
