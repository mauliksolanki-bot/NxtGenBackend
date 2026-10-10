package com.nxtgen.api.dto;

/**
 * Powers the Service Catalog tile pages (categories now, sub-categories and
 * service items follow the same shape later).
 */
public class SvcCategoryResponse {

    private final Long id;
    private final String name;
    private final String icon;
    private final String description;
    private final String slug;

    public SvcCategoryResponse(Long id, String name, String icon, String description, String slug) {
        this.id = id;
        this.name = name;
        this.icon = icon;
        this.description = description;
        this.slug = slug;
    }

    public Long getId() {
        return id;
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
