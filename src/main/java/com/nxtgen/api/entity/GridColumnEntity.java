package com.nxtgen.api.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "NXTGEN_GRID_COLUMN")
public class GridColumnEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "GRID_ID", nullable = false)
    private Long gridId;

    @Column(name = "COLUMN_NAME", nullable = false)
    private String columnName;

    @Column(name = "DATA_FIELD", nullable = false)
    private String dataField;

    @Column(name = "WIDTH")
    private Long width;

    @Column(name = "DISPLAY_ORDER")
    private Long displayOrder;

    @Column(name = "IS_SORTABLE")
    private String isSortable;

    @Column(name = "IS_FILTERABLE")
    private String isFilterable;

    @Column(name = "IS_HIDDEN")
    private String isHidden;

    @Column(name = "CRE_BY")
    private String creBy;

    @Column(name = "CRE_DATE")
    private LocalDateTime creDate;

    public Long getId() {
        return id;
    }

    public Long getGridId() {
        return gridId;
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

    public String getIsSortable() {
        return isSortable;
    }

    public String getIsFilterable() {
        return isFilterable;
    }

    public String getIsHidden() {
        return isHidden;
    }

    public String getCreBy() {
        return creBy;
    }

    public LocalDateTime getCreDate() {
        return creDate;
    }
}
