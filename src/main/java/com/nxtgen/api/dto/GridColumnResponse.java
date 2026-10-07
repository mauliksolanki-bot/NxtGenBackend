package com.nxtgen.api.dto;

public class GridColumnResponse {

    private final Long id;
    private final String columnName;
    private final String dataField;
    private final Long width;
    private final Long displayOrder;
    private final boolean sortable;
    private final boolean filterable;
    private final boolean hidden;

    public GridColumnResponse(
            Long id,
            String columnName,
            String dataField,
            Long width,
            Long displayOrder,
            boolean sortable,
            boolean filterable,
            boolean hidden
    ) {
        this.id = id;
        this.columnName = columnName;
        this.dataField = dataField;
        this.width = width;
        this.displayOrder = displayOrder;
        this.sortable = sortable;
        this.filterable = filterable;
        this.hidden = hidden;
    }

    public Long getId() {
        return id;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getDataField() {
        return dataField;
    }

    public Long getWidth() {
        return width;
    }

    public Long getDisplayOrder() {
        return displayOrder;
    }

    public boolean isSortable() {
        return sortable;
    }

    public boolean isFilterable() {
        return filterable;
    }

    public boolean isHidden() {
        return hidden;
    }
}
