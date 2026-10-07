package com.nxtgen.api.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.nxtgen.api.dto.NavMenuResponse;
import com.nxtgen.api.entity.NavMenuEntity;
import com.nxtgen.api.repository.NavMenuRepository;

import static com.nxtgen.api.constant.NxtGenCommonConstant.CHILD_TYPE;
import static com.nxtgen.api.constant.NxtGenCommonConstant.ENABLED_FLAG;

@Service
public class NavMenuService {

    private final NavMenuRepository navMenuRepository;

    public NavMenuService(NavMenuRepository navMenuRepository) {
        this.navMenuRepository = navMenuRepository;
    }

    public List<NavMenuResponse> getNavigationMenu() {
        List<NavMenuEntity> rows = navMenuRepository.findByIsEnabledOrderByIdAsc(ENABLED_FLAG);

        Map<Long, NavMenuResponse> nodesById = rows.stream()
                .collect(Collectors.toMap(
                        NavMenuEntity::getId,
                        this::toResponse,
                        (existing, replacement) -> existing,
                        LinkedHashMap::new
                ));

        Map<Boolean, List<NavMenuEntity>> partitionedByAttachable = rows.stream()
                .collect(Collectors.partitioningBy(row ->
                        isChild(row) && row.getParentMenuId() != null && nodesById.containsKey(row.getParentMenuId())
                ));

        partitionedByAttachable.get(true).forEach(row ->
                nodesById.get(row.getParentMenuId()).getChildren().add(nodesById.get(row.getId()))
        );

        return partitionedByAttachable.get(false).stream()
                .map(row -> nodesById.get(row.getId()))
                .collect(Collectors.toList());
    }

    private boolean isChild(NavMenuEntity row) {
        return row.getMenuType() != null
                && CHILD_TYPE.equalsIgnoreCase(row.getMenuType().trim().toUpperCase(Locale.ROOT));
    }

    private NavMenuResponse toResponse(NavMenuEntity row) {
        return new NavMenuResponse(
                row.getId(),
                row.getMenuCode(),
                row.getMenuName(),
                row.getMenuDisplayName(),
                row.getMenuUrl(),
                row.getMenuType()
        );
    }
}
