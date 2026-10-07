package com.nxtgen.api.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.nxtgen.api.entity.UserMasterEntity;
import com.nxtgen.api.exception.GridNotFoundException;
import com.nxtgen.api.repository.UserMasterRepository;

import static com.nxtgen.api.constant.NxtGenCommonConstant.USERS_GRID;

@Service
public class GridDataService {

    private static final String GRID_NOT_FOUND_MESSAGE = "Grid data is unavailable right now.";

    private final UserMasterRepository userMasterRepository;
    private final Map<String, Supplier<List<Map<String, Object>>>> gridDataSuppliers;

    public GridDataService(UserMasterRepository userMasterRepository) {
        this.userMasterRepository = userMasterRepository;
        this.gridDataSuppliers = Map.of(
                USERS_GRID, this::fetchUsersGridData
        );
    }

    public List<Map<String, Object>> getGridData(String gridName) {
        return Optional.ofNullable(gridName)
                .map(name -> name.trim().toUpperCase(Locale.ROOT))
                .map(gridDataSuppliers::get)
                .orElseThrow(() -> new GridNotFoundException(GRID_NOT_FOUND_MESSAGE))
                .get();
    }

    /**
     * Reads the Administration > Users grid data directly from NXTGEN_USERS_MASTER.
     * Keys match the DATA_FIELD values configured for the USERS_GRID columns so the
     * frontend can bind rows to columns without any extra mapping.
     */
    private List<Map<String, Object>> fetchUsersGridData() {
        return userMasterRepository.findAll().stream()
                .map(this::toUsersGridRow)
                .collect(Collectors.toList());
    }

    private Map<String, Object> toUsersGridRow(UserMasterEntity user) {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("id", user.getId());
        row.put("emplNm", user.getEmplNm());
        row.put("username", user.getUsername());
        row.put("emailAddress", user.getEmailAddress());
        row.put("actvFlag", user.getActvFlag());
        row.put("isLocked", user.getIsLocked());
        return row;
    }
}
