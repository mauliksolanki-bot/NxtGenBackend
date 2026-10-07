package com.nxtgen.api.dto;

import java.util.List;

public class GridConfigResponse {

    private final Long id;
    private final String gridName;
    private final String dataApi;
    private final String loadingMessage;
    private final List<GridColumnResponse> columns;

    public GridConfigResponse(
            Long id,
            String gridName,
            String dataApi,
            String loadingMessage,
            List<GridColumnResponse> columns
    ) {
        this.id = id;
        this.gridName = gridName;
        this.dataApi = dataApi;
        this.loadingMessage = loadingMessage;
        this.columns = columns;
    }

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

    public List<GridColumnResponse> getColumns() {
        return columns;
    }
}
