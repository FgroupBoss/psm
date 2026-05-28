package com.fgroupboss.ai.psm.location.controller;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.location.model.dto.LocGeofenceRequest;
import com.fgroupboss.ai.psm.location.model.vo.LocGeofenceVO;
import com.fgroupboss.ai.psm.location.service.LocGeofenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 电子围栏维护接口。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/location/geofences")
public class LocGeofenceController {

    private final LocGeofenceService geofenceService;

    @GetMapping
    public ResponseVO<PageResult<LocGeofenceVO>> page(@RequestParam Long tenantId,
                                                      @RequestParam(required = false) String keyword,
                                                      @RequestParam(required = false) String fenceType,
                                                      @RequestParam(defaultValue = "1") int pageNo,
                                                      @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseVO.success(geofenceService.page(tenantId, keyword, fenceType, pageNo, pageSize));
    }

    @PostMapping
    public ResponseVO<LocGeofenceVO> create(@Valid @RequestBody LocGeofenceRequest request) {
        return ResponseVO.success(geofenceService.create(request));
    }
}
