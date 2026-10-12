package com.nxtgen.api.controller;

import java.util.List;
import org.springframework.web.bind.annotation.*;
import com.nxtgen.api.service.MyApprovalsService;

@RestController
@RequestMapping("/api/myapprovals")
public class NxtGenMyApprovalsController {
    private final MyApprovalsService service;
    public NxtGenMyApprovalsController(MyApprovalsService service) { this.service = service; }
    @GetMapping
    public List<MyApprovalsService.Approval> list(@RequestHeader(value = "Authorization", required = false) String authorization) {
        return service.list(authorization);
    }
}
