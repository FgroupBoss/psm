package com.fgroupboss.ai.psm.risk.inspection.controller;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.risk.inspection.model.dto.InspectionPlanRequest;
import com.fgroupboss.ai.psm.risk.inspection.model.vo.InspectionPlanVO;
import com.fgroupboss.ai.psm.risk.inspection.service.InspectionPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import javax.validation.Valid;

/**
 * 巡检计划接口。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/inspection/plans")
public class InspectionPlanController {

    private final InspectionPlanService planService;

    @GetMapping
    public ResponseVO<PageResult<InspectionPlanVO>> page(@RequestParam Long tenantId,
                                                         @RequestParam(required = false) String keyword,
                                                         @RequestParam(defaultValue = "1") int pageNo,
                                                         @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseVO.success(planService.page(tenantId, keyword, pageNo, pageSize));
    }

    @GetMapping("/by-major-hazard")
    public ResponseVO<List<InspectionPlanVO>> listByMajorHazard(@RequestParam Long tenantId,
                                                                  @RequestParam Long majorHazardId) {
        return ResponseVO.success(planService.listByMajorHazard(tenantId, majorHazardId));
    }

    @GetMapping("/{id}")
    public ResponseVO<InspectionPlanVO> get(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(planService.getById(tenantId, id));
    }

    @PostMapping
    public ResponseVO<InspectionPlanVO> create(@Valid @RequestBody InspectionPlanRequest request) {
        return ResponseVO.success(planService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseVO<InspectionPlanVO> update(@PathVariable Long id,
                                               @Valid @RequestBody InspectionPlanRequest request) {
        return ResponseVO.success(planService.update(id, request));
    }
}
