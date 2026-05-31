package com.fgroupboss.ai.psm.realtime.location.controller;

import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.realtime.location.model.vo.LocRealtimeVO;
import com.fgroupboss.ai.psm.realtime.location.model.vo.LocTrackPointVO;
import com.fgroupboss.ai.psm.realtime.location.service.LocLocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 实时位置与历史轨迹查询。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/location/locations")
public class LocLocationController {

    private final LocLocationService locationService;

    @GetMapping("/realtime")
    public ResponseVO<List<LocRealtimeVO>> realtime(@RequestParam Long tenantId,
                                                  @RequestParam(required = false) Long areaId,
                                                  @RequestParam(required = false) Long personId,
                                                  @RequestParam(required = false) String tagNo,
                                                  @RequestParam(required = false) String onlineStatus) {
        return ResponseVO.success(locationService.listRealtime(tenantId, areaId, personId, tagNo, onlineStatus));
    }

    @GetMapping("/tracks")
    public ResponseVO<List<LocTrackPointVO>> tracks(@RequestParam Long tenantId,
                                                   @RequestParam(required = false) String tagNo,
                                                   @RequestParam(required = false) Long personId,
                                                   @RequestParam(required = false) Long areaId,
                                                   @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromTime,
                                                   @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toTime,
                                                   @RequestParam(defaultValue = "1000") int limit) {
        return ResponseVO.success(locationService.listTracks(tenantId, tagNo, personId, areaId, fromTime, toTime, limit));
    }
}
