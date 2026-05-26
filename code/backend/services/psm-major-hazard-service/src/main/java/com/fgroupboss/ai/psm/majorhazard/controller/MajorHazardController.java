package com.fgroupboss.ai.psm.majorhazard.controller;

import com.fgroupboss.ai.psm.common.DemoInfo;
import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.majorhazard.model.vo.MajorHazardVO;
import com.fgroupboss.ai.psm.majorhazard.service.MajorHazardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 重大危险源管理接口（批次 1：健康检查与分页查询骨架）。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/major-hazards")
public class MajorHazardController {

    private final MajorHazardService hazardService;

    @GetMapping("/health")
    public ResponseVO<DemoInfo> health() {
        return ResponseVO.success(new DemoInfo("psm-major-hazard-service", "major-hazard", "1.0.0-batch1"));
    }

    @GetMapping
    public ResponseVO<PageResult<MajorHazardVO>> page(@RequestParam Long tenantId,
                                                      @RequestParam(required = false) String keyword,
                                                      @RequestParam(required = false) String status,
                                                      @RequestParam(required = false) String level,
                                                      @RequestParam(defaultValue = "1") int pageNo,
                                                      @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseVO.success(hazardService.page(tenantId, keyword, status, level, pageNo, pageSize));
    }

    @GetMapping("/{id}")
    public ResponseVO<MajorHazardVO> get(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(hazardService.getById(tenantId, id));
    }
}
