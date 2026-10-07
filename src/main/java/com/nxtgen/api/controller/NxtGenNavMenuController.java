package com.nxtgen.api.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nxtgen.api.dto.NavMenuResponse;
import com.nxtgen.api.service.NavMenuService;

@RestController
@RequestMapping("/api/navmenu")
public class NxtGenNavMenuController {

    private final NavMenuService navMenuService;

    public NxtGenNavMenuController(NavMenuService navMenuService) {
        this.navMenuService = navMenuService;
    }

    @GetMapping
    public List<NavMenuResponse> getNavigationMenu(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader
    ) {
        return navMenuService.getNavigationMenu(authorizationHeader);
    }
}
