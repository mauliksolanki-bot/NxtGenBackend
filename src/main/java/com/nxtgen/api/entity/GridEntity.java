package com.nxtgen.api.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "NXTGEN_GRID")
public class GridEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "GRID_NAME", nullable = false)
    private String gridName;

    @Column(name = "DATA_API")
    private String dataApi;

    @Column(name = "LOADING_MESSAGE")
    private String loadingMessage;

    @Column(name = "CRE_BY")
    private String creBy;

    @Column(name = "CRE_DATE")
    private LocalDateTime creDate;

    public Long getId() {
        return id;
    }

    public String getGridName() {
        return gridName;
    }

    public String getDataApi() {
        return dataApi;
    }

    public String getLoadingMessage() {
        return loadingMessage;
    }

    public String getCreBy() {
        return creBy;
    }

    public LocalDateTime getCreDate() {
        return creDate;
    }
}
