package com.nxtgen.api.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.nxtgen.api.entity.RoleEntity;
import com.nxtgen.api.entity.GroupEntity;
import com.nxtgen.api.entity.UserMasterEntity;
import com.nxtgen.api.exception.GridNotFoundException;
import com.nxtgen.api.repository.GroupRepository;
import com.nxtgen.api.repository.RoleRepository;
import com.nxtgen.api.repository.UserMasterRepository;

import static com.nxtgen.api.constant.NxtGenCommonConstant.GROUPS_TEAMS_GRID;
import static com.nxtgen.api.constant.NxtGenCommonConstant.ROLES_GRID;
import static com.nxtgen.api.constant.NxtGenCommonConstant.USERS_GRID;

@Service
public class GridDataService {

    private static final String GRID_NOT_FOUND_MESSAGE = "Grid data is unavailable right now.";

    private final UserMasterRepository userMasterRepository;
    private final RoleRepository roleRepository;
    private final GroupRepository groupRepository;
    private final Map<String, Supplier<List<Map<String, Object>>>> gridDataSuppliers;

    public GridDataService(UserMasterRepository userMasterRepository, RoleRepository roleRepository, GroupRepository groupRepository) {
        this.userMasterRepository = userMasterRepository;
        this.roleRepository = roleRepository;
        this.groupRepository = groupRepository;
        this.gridDataSuppliers = Map.of(
                USERS_GRID, this::fetchUsersGridData,
                ROLES_GRID, this::fetchRolesGridData,
                GROUPS_TEAMS_GRID, this::fetchGroupsTeamsGridData
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

    /**
     * Reads the Administration > Roles grid data directly from NXTGEN_ROLES.
     * Keys match the DATA_FIELD values configured for the ROLES_GRID columns so the
     * frontend can bind rows to columns without any extra mapping.
     */
    private List<Map<String, Object>> fetchRolesGridData() {
        return roleRepository.findAll().stream()
                .map(this::toRolesGridRow)
                .collect(Collectors.toList());
    }

    private Map<String, Object> toRolesGridRow(RoleEntity role) {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("id", role.getId());
        row.put("roleName", role.getRoleName());
        row.put("description", role.getDescription());
        row.put("isActive", role.getIsActive());
        return row;
    }

    /**
     * Reads the Administration > Groups & Teams grid data directly from
     * NXTGEN_GROUPS_ND_TEAMS. Keys match the DATA_FIELD values configured for
     * the NXTGEN_GRP_TEAMS grid columns so the frontend can bind rows to
     * columns without any extra mapping.
     */
    private List<Map<String, Object>> fetchGroupsTeamsGridData() {
        return groupRepository.findAll().stream()
                .map(this::toGroupsTeamsGridRow)
                .collect(Collectors.toList());
    }

    private Map<String, Object> toGroupsTeamsGridRow(GroupEntity group) {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("id", group.getId());
        row.put("grpName", group.getGrpName());
        row.put("grpDescription", group.getGrpDescription());
        row.put("groupOwner", group.getGroupOwner());
        row.put("isActive", group.getIsActive());
        return row;
    }
}
