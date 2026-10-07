package com.nxtgen.api.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.nxtgen.api.dto.GridColumnResponse;
import com.nxtgen.api.dto.GridConfigResponse;
import com.nxtgen.api.entity.GridColumnEntity;
import com.nxtgen.api.entity.GridEntity;
import com.nxtgen.api.exception.GridNotFoundException;
import com.nxtgen.api.repository.GridColumnRepository;
import com.nxtgen.api.repository.GridRepository;

import static com.nxtgen.api.constant.NxtGenCommonConstant.ENABLED_FLAG;

@Service
public class GridConfigService {

    private static final String GRID_NOT_FOUND_MESSAGE = "Grid configuration is unavailable right now.";

    private final GridRepository gridRepository;
    private final GridColumnRepository gridColumnRepository;

    public GridConfigService(GridRepository gridRepository, GridColumnRepository gridColumnRepository) {
        this.gridRepository = gridRepository;
        this.gridColumnRepository = gridColumnRepository;
    }

    public GridConfigResponse getGridConfig(String gridName) {
        GridEntity grid = gridRepository.findByGridName(gridName)
                .orElseThrow(() -> new GridNotFoundException(GRID_NOT_FOUND_MESSAGE));

        List<GridColumnResponse> columns = gridColumnRepository.findByGridIdOrderByDisplayOrderAsc(grid.getId())
                .stream()
                .map(this::toColumnResponse)
                .collect(Collectors.toList());

        return new GridConfigResponse(
                grid.getId(),
                grid.getGridName(),
                grid.getDataApi(),
                grid.getLoadingMessage(),
                columns
        );
    }

    private GridColumnResponse toColumnResponse(GridColumnEntity column) {
        return new GridColumnResponse(
                column.getId(),
                column.getColumnName(),
                column.getDataField(),
                column.getWidth(),
                column.getDisplayOrder(),
                ENABLED_FLAG.equalsIgnoreCase(column.getIsSortable()),
                ENABLED_FLAG.equalsIgnoreCase(column.getIsFilterable()),
                ENABLED_FLAG.equalsIgnoreCase(column.getIsHidden())
        );
    }
}
