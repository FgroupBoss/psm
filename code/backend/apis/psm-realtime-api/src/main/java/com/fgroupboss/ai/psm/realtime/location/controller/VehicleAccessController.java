package com.fgroupboss.ai.psm.realtime.location.controller;

import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.realtime.location.model.dto.VehicleRecordIngestRequest;
import com.fgroupboss.ai.psm.realtime.location.model.vo.VehicleAccessRecordVO;
import com.fgroupboss.ai.psm.realtime.location.service.VehicleAccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * VehicleAccess 模块 HTTP API。
 * <p>人员/车辆/访客定位与出入记录。</p>
 * <p>基础路径：{@code /api/location/vehicles/records}</p>
 * <p>返回体均为 {@link com.fgroupboss.ai.psm.common.ResponseVO}；写操作需透传租户与操作人上下文。</p>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/location/vehicles/records")
public class VehicleAccessController {

    private final VehicleAccessService vehicleAccessService;

    /**
     * 新增ingest或触发ingest相关动作。
     * <p>HTTP POST {@code /api/location/vehicles/records/ingest}</p>
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/ingest")
    public ResponseVO<VehicleAccessRecordVO> ingest(@Valid @RequestBody VehicleRecordIngestRequest request) {
        return ResponseVO.success(vehicleAccessService.ingest(request));
    }
}
