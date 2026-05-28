package com.fgroupboss.ai.psm.location.controller;

import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.location.model.dto.VehicleRecordIngestRequest;
import com.fgroupboss.ai.psm.location.model.vo.VehicleAccessRecordVO;
import com.fgroupboss.ai.psm.location.service.VehicleAccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 车辆进出记录接入。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/location/vehicles/records")
public class VehicleAccessController {

    private final VehicleAccessService vehicleAccessService;

    @PostMapping("/ingest")
    public ResponseVO<VehicleAccessRecordVO> ingest(@Valid @RequestBody VehicleRecordIngestRequest request) {
        return ResponseVO.success(vehicleAccessService.ingest(request));
    }
}
