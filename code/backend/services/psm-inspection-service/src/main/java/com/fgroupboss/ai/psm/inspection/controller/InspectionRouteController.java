package com.fgroupboss.ai.psm.inspection.controller;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.inspection.model.dto.InspectionRouteRequest;
import com.fgroupboss.ai.psm.inspection.model.vo.InspectionRouteVO;
import com.fgroupboss.ai.psm.inspection.service.InspectionRouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 巡检路线接口。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/inspection/routes")
public class InspectionRouteController {

    private final InspectionRouteService routeService;

    @GetMapping
    public ResponseVO<PageResult<InspectionRouteVO>> page(@RequestParam Long tenantId,
                                                          @RequestParam(required = false) String keyword,
                                                          @RequestParam(defaultValue = "1") int pageNo,
                                                          @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseVO.success(routeService.page(tenantId, keyword, pageNo, pageSize));
    }

    @GetMapping("/{id}")
    public ResponseVO<InspectionRouteVO> get(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(routeService.getById(tenantId, id));
    }

    @PostMapping
    public ResponseVO<InspectionRouteVO> create(@Valid @RequestBody InspectionRouteRequest request) {
        return ResponseVO.success(routeService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseVO<InspectionRouteVO> update(@PathVariable Long id,
                                              @Valid @RequestBody InspectionRouteRequest request) {
        return ResponseVO.success(routeService.update(id, request));
    }
}
