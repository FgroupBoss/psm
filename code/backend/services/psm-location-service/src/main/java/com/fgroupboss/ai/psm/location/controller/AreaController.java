package com.fgroupboss.ai.psm.location.controller;

import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.location.model.vo.AreaHeadcountVO;
import com.fgroupboss.ai.psm.location.service.LocLocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 区域封闭化统计接口。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/location/areas")
public class AreaController {

    private final LocLocationService locationService;

    @GetMapping("/{id}/headcount")
    public ResponseVO<AreaHeadcountVO> headcount(@PathVariable("id") Long areaId, @RequestParam Long tenantId) {
        return ResponseVO.success(locationService.headcount(tenantId, areaId));
    }
}
