package com.fgroupboss.ai.psm.processsafety.pha.controller;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.processsafety.pha.model.dto.LopaScenarioRequest;
import com.fgroupboss.ai.psm.processsafety.pha.model.vo.LopaCalculateResultVO;
import com.fgroupboss.ai.psm.processsafety.pha.model.vo.LopaScenarioVO;
import com.fgroupboss.ai.psm.processsafety.pha.service.LopaScenarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pha/lopa-scenarios")
public class LopaScenarioController {

    private final LopaScenarioService lopaScenarioService;

    @GetMapping
    public ResponseVO<PageResult<LopaScenarioVO>> page(@RequestParam Long tenantId,
                                                         @RequestParam(required = false) Long projectId,
                                                         @RequestParam(defaultValue = "1") int pageNo,
                                                         @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseVO.success(lopaScenarioService.page(tenantId, projectId, pageNo, pageSize));
    }

    @PostMapping
    public ResponseVO<LopaScenarioVO> create(@Valid @RequestBody LopaScenarioRequest request,
                                             @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(lopaScenarioService.create(request, operator));
    }

    @PostMapping("/{id}/calculate")
    public ResponseVO<LopaCalculateResultVO> calculate(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(lopaScenarioService.calculate(id, tenantId));
    }
}
