package com.fgroupboss.ai.psm.location.controller;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.location.model.dto.GateRecordIngestRequest;
import com.fgroupboss.ai.psm.location.model.vo.GateAccessRecordVO;
import com.fgroupboss.ai.psm.location.service.GateAccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDateTime;

/**
 * 门禁进出记录接口。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/location/gate/records")
public class GateAccessController {

    private final GateAccessService gateAccessService;

    @GetMapping
    public ResponseVO<PageResult<GateAccessRecordVO>> page(@RequestParam Long tenantId,
                                                           @RequestParam(required = false) String gateCode,
                                                           @RequestParam(required = false) Long personId,
                                                           @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromTime,
                                                           @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toTime,
                                                           @RequestParam(defaultValue = "1") int pageNo,
                                                           @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseVO.success(gateAccessService.page(tenantId, gateCode, personId, fromTime, toTime, pageNo, pageSize));
    }

    @PostMapping("/ingest")
    public ResponseVO<GateAccessRecordVO> ingest(@Valid @RequestBody GateRecordIngestRequest request) {
        return ResponseVO.success(gateAccessService.ingest(request));
    }
}
