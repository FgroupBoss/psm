package com.fgroupboss.ai.psm.realtime.location.controller;

import com.fgroupboss.ai.psm.common.LoginContext;
import com.fgroupboss.ai.psm.common.UserContext;
import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.realtime.location.model.vo.AreaHeadcountVO;
import com.fgroupboss.ai.psm.realtime.location.service.LocLocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Area 模块 HTTP API。
 * <p>人员/车辆/访客定位与出入记录。</p>
 * <p>基础路径：{@code /api/location/areas}</p>
 * <p>返回体均为 {@link com.fgroupboss.ai.psm.common.ResponseVO}；写操作需透传租户与操作人上下文。</p>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/location/areas")
public class AreaController {

    private final LocLocationService locationService;

    /**
     * 查询headcount。
     * <p>HTTP GET {@code /api/location/areas/{id}/headcount}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param areaId 区域 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/{id}/headcount")
    public ResponseVO<AreaHeadcountVO> headcount(@LoginContext UserContext loginContext,
                                        @PathVariable("id") Long areaId) {
        return ResponseVO.success(locationService.headcount(loginContext.getTenantId(), areaId));
    }
}
