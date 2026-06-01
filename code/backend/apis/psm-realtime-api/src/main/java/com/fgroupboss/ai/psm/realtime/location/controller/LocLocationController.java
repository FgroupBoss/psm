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
 * LocLocation 模块 HTTP API。
 * <p>人员/车辆/访客定位与出入记录。</p>
 * <p>基础路径：{@code /api/location/locations}</p>
 * <p>返回体均为 {@link com.fgroupboss.ai.psm.common.ResponseVO}；写操作需透传租户与操作人上下文。</p>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/location/locations")
public class LocLocationController {

    private final LocLocationService locationService;

    /**
     * 查询realtime。
     * <p>HTTP GET {@code /api/location/locations/realtime}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param tenantId 租户 ID，多租户隔离必填
     * @param areaId 区域 ID
     * @param personId person ID
     * @param tagNo tagNo 参数
     * @param onlineStatus onlineStatus 参数
     * @return 列表数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/realtime")
    public ResponseVO<List<LocRealtimeVO>> realtime(@RequestParam Long tenantId,
                                                  @RequestParam(required = false) Long areaId,
                                                  @RequestParam(required = false) Long personId,
                                                  @RequestParam(required = false) String tagNo,
                                                  @RequestParam(required = false) String onlineStatus) {
        return ResponseVO.success(locationService.listRealtime(tenantId, areaId, personId, tagNo, onlineStatus));
    }

    /**
     * 查询tracks。
     * <p>HTTP GET {@code /api/location/locations/tracks}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param tenantId 租户 ID，多租户隔离必填
     * @param tagNo tagNo 参数
     * @param personId person ID
     * @param areaId 区域 ID
     * @param fromTime fromTime 参数
     * @param toTime toTime 参数
     * @param limit limit 参数
     * @return 列表数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
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
