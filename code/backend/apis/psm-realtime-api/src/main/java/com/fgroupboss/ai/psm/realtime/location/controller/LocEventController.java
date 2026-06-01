package com.fgroupboss.ai.psm.realtime.location.controller;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.realtime.location.model.dto.LocEventIngestRequest;
import com.fgroupboss.ai.psm.realtime.location.model.vo.LocEventVO;
import com.fgroupboss.ai.psm.realtime.location.service.LocEventService;
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
 * 定位事件接入与查询。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/location/events")
public class LocEventController {

    private final LocEventService eventService;

    @PostMapping("/ingest")
    public ResponseVO<LocEventVO> ingest(@Valid @RequestBody LocEventIngestRequest request) {
        return ResponseVO.success(eventService.ingest(request));
    }

    @GetMapping
    public ResponseVO<PageResult<LocEventVO>> page(@RequestParam Long tenantId,
                                                   @RequestParam(required = false) String eventType,
                                                   @RequestParam(required = false) String tagNo,
                                                   @RequestParam(required = false) Long personId,
                                                   @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromTime,
                                                   @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toTime,
                                                   @RequestParam(defaultValue = "1") int pageNo,
                                                   @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseVO.success(eventService.page(tenantId, eventType, tagNo, personId, fromTime, toTime, pageNo, pageSize));
    }
}
