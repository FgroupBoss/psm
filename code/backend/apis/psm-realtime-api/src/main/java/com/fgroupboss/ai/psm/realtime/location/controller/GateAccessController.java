package com.fgroupboss.ai.psm.realtime.location.controller;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.realtime.location.model.dto.GateRecordIngestRequest;
import com.fgroupboss.ai.psm.realtime.location.model.vo.GateAccessRecordVO;
import com.fgroupboss.ai.psm.realtime.location.service.GateAccessService;
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
 * GateAccess 模块 HTTP API。
 * <p>人员/车辆/访客定位与出入记录。</p>
 * <p>基础路径：{@code /api/location/gate/records}</p>
 * <p>返回体均为 {@link com.fgroupboss.ai.psm.common.ResponseVO}；写操作需透传租户与操作人上下文。</p>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/location/gate/records")
public class GateAccessController {

    private final GateAccessService gateAccessService;

    /**
     * 分页查询列表。
     * <p>HTTP GET {@code /api/location/gate/records}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param tenantId 租户 ID，多租户隔离必填
     * @param gateCode gateCode 参数
     * @param personId person ID
     * @param fromTime fromTime 参数
     * @param toTime toTime 参数
     * @param pageNo 页码，从 1 开始
     * @param pageSize 每页条数，默认 20
     * @return 分页数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
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

    /**
     * 新增ingest或触发ingest相关动作。
     * <p>HTTP POST {@code /api/location/gate/records/ingest}</p>
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/ingest")
    public ResponseVO<GateAccessRecordVO> ingest(@Valid @RequestBody GateRecordIngestRequest request) {
        return ResponseVO.success(gateAccessService.ingest(request));
    }
}
