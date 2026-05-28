package com.fgroupboss.ai.psm.dualprevention.controller;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.common.UserContextHeaders;
import com.fgroupboss.ai.psm.common.UserContextResolver;
import com.fgroupboss.ai.psm.dualprevention.model.dto.HazardAreaOpenCheckRequest;
import com.fgroupboss.ai.psm.dualprevention.model.dto.HazardConfirmRequest;
import com.fgroupboss.ai.psm.dualprevention.model.dto.HazardEscalateRequest;
import com.fgroupboss.ai.psm.dualprevention.model.dto.HazardOverdueCheckRequest;
import com.fgroupboss.ai.psm.dualprevention.model.dto.HazardRectifyRequest;
import com.fgroupboss.ai.psm.dualprevention.model.dto.HazardReportRequest;
import com.fgroupboss.ai.psm.dualprevention.model.dto.HazardReviewRequest;
import com.fgroupboss.ai.psm.dualprevention.model.vo.HazardAreaOpenCheckVO;
import com.fgroupboss.ai.psm.dualprevention.model.vo.HazardOverdueCheckVO;
import com.fgroupboss.ai.psm.dualprevention.model.vo.HazardReportVO;
import com.fgroupboss.ai.psm.dualprevention.model.vo.HazardStatisticsVO;
import com.fgroupboss.ai.psm.dualprevention.service.HazardReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 隐患上报与治理接口。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dual-prevention/hazards")
public class HazardController {

    private final HazardReportService hazardReportService;

    @GetMapping
    public ResponseVO<PageResult<HazardReportVO>> page(@RequestParam Long tenantId,
                                                       @RequestParam(required = false) String keyword,
                                                       @RequestParam(required = false) String status,
                                                       @RequestParam(required = false) String hazardLevel,
                                                       @RequestParam(required = false) Long areaId,
                                                       @RequestParam(required = false) Long riskUnitId,
                                                       @RequestParam(required = false) Integer overdueFlag,
                                                       @RequestParam(defaultValue = "1") int pageNo,
                                                       @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseVO.success(hazardReportService.page(tenantId, keyword, status, hazardLevel,
                areaId, riskUnitId, overdueFlag, pageNo, pageSize));
    }

    @GetMapping("/statistics")
    public ResponseVO<HazardStatisticsVO> statistics(@RequestParam Long tenantId,
                                                       @RequestParam(required = false) Long areaId) {
        return ResponseVO.success(hazardReportService.statistics(tenantId, areaId));
    }

    @GetMapping("/{id}")
    public ResponseVO<HazardReportVO> get(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(hazardReportService.getById(tenantId, id));
    }

    @PostMapping
    public ResponseVO<HazardReportVO> create(@Valid @RequestBody HazardReportRequest request,
                                             @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                             @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                             @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(hazardReportService.create(request, operator(userId, username, operator)));
    }

    @PostMapping("/{id}/confirm")
    public ResponseVO<HazardReportVO> confirm(@PathVariable Long id,
                                              @Valid @RequestBody HazardConfirmRequest request,
                                              @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                              @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                              @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(hazardReportService.confirm(id, request, operator(userId, username, operator)));
    }

    @PostMapping("/{id}/rectify")
    public ResponseVO<HazardReportVO> rectify(@PathVariable Long id,
                                              @Valid @RequestBody HazardRectifyRequest request,
                                              @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                              @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                              @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(hazardReportService.rectify(id, request, operator(userId, username, operator)));
    }

    @PostMapping("/{id}/review")
    public ResponseVO<HazardReportVO> review(@PathVariable Long id,
                                             @Valid @RequestBody HazardReviewRequest request,
                                             @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                             @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                             @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(hazardReportService.review(id, request, operator(userId, username, operator)));
    }

    @PostMapping("/overdue-check")
    public ResponseVO<HazardOverdueCheckVO> overdueCheck(@Valid @RequestBody HazardOverdueCheckRequest request) {
        return ResponseVO.success(hazardReportService.overdueCheck(request));
    }

    @PostMapping("/area-open-check")
    public ResponseVO<HazardAreaOpenCheckVO> areaOpenCheck(@Valid @RequestBody HazardAreaOpenCheckRequest request) {
        return ResponseVO.success(hazardReportService.areaOpenCheck(request));
    }

    @PostMapping("/{id}/escalate")
    public ResponseVO<HazardReportVO> escalate(@PathVariable Long id,
                                               @Valid @RequestBody HazardEscalateRequest request,
                                               @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                               @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                               @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(hazardReportService.escalate(id, request, operator(userId, username, operator)));
    }

    private String operator(String userId, String username, String fallback) {
        return UserContextResolver.operator(userId, username, fallback);
    }
}
