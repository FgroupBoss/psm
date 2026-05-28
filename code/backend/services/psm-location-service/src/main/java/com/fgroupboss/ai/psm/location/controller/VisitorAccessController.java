package com.fgroupboss.ai.psm.location.controller;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.location.model.dto.VisitorRecordIngestRequest;
import com.fgroupboss.ai.psm.location.model.vo.VisitorAccessRecordVO;
import com.fgroupboss.ai.psm.location.service.VisitorAccessService;
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
 * 访客进出记录接口。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/location/visitors/records")
public class VisitorAccessController {

    private final VisitorAccessService visitorAccessService;

    @GetMapping
    public ResponseVO<PageResult<VisitorAccessRecordVO>> page(@RequestParam Long tenantId,
                                                              @RequestParam(required = false) String visitorName,
                                                              @RequestParam(required = false) String gateCode,
                                                              @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromTime,
                                                              @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toTime,
                                                              @RequestParam(defaultValue = "1") int pageNo,
                                                              @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseVO.success(visitorAccessService.page(tenantId, visitorName, gateCode, fromTime, toTime, pageNo, pageSize));
    }

    @PostMapping("/ingest")
    public ResponseVO<VisitorAccessRecordVO> ingest(@Valid @RequestBody VisitorRecordIngestRequest request) {
        return ResponseVO.success(visitorAccessService.ingest(request));
    }
}
