package com.fgroupboss.ai.psm.risk.dualprevention.controller;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.common.UserContextHeaders;
import com.fgroupboss.ai.psm.common.UserContextResolver;
import com.fgroupboss.ai.psm.risk.dualprevention.model.dto.HazardAreaOpenCheckRequest;
import com.fgroupboss.ai.psm.risk.dualprevention.model.dto.HazardConfirmRequest;
import com.fgroupboss.ai.psm.risk.dualprevention.model.dto.HazardEscalateRequest;
import com.fgroupboss.ai.psm.risk.dualprevention.model.dto.HazardOverdueCheckRequest;
import com.fgroupboss.ai.psm.risk.dualprevention.model.dto.HazardRectifyRequest;
import com.fgroupboss.ai.psm.risk.dualprevention.model.dto.HazardReportRequest;
import com.fgroupboss.ai.psm.risk.dualprevention.model.dto.HazardReviewRequest;
import com.fgroupboss.ai.psm.risk.dualprevention.model.vo.HazardAreaOpenCheckVO;
import com.fgroupboss.ai.psm.risk.dualprevention.model.vo.HazardOverdueCheckVO;
import com.fgroupboss.ai.psm.risk.dualprevention.model.vo.HazardReportVO;
import com.fgroupboss.ai.psm.risk.dualprevention.model.vo.HazardStatisticsVO;
import com.fgroupboss.ai.psm.risk.dualprevention.service.HazardReportService;
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
 * Hazard 模块 HTTP API。
 * <p>双重预防机制：风险单元、事件与管控措施。</p>
 * <p>基础路径：{@code /api/dual-prevention/hazards}</p>
 * <p>返回体均为 {@link com.fgroupboss.ai.psm.common.ResponseVO}；写操作需透传租户与操作人上下文。</p>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dual-prevention/hazards")
public class HazardController {

    private final HazardReportService hazardReportService;

    /**
     * 分页查询列表。
     * <p>HTTP GET {@code /api/dual-prevention/hazards}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param tenantId 租户 ID，多租户隔离必填
     * @param keyword 模糊搜索关键字
     * @param status 业务状态筛选
     * @param hazardLevel hazardLevel 参数
     * @param areaId 区域 ID
     * @param riskUnitId riskUnit ID
     * @param overdueFlag overdueFlag 参数
     * @param pageNo 页码，从 1 开始
     * @param pageSize 每页条数，默认 20
     * @return 分页数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
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

    /**
     * 查询statistics。
     * <p>HTTP GET {@code /api/dual-prevention/hazards/statistics}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param tenantId 租户 ID，多租户隔离必填
     * @param areaId 区域 ID
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/statistics")
    public ResponseVO<HazardStatisticsVO> statistics(@RequestParam Long tenantId,
                                                       @RequestParam(required = false) Long areaId) {
        return ResponseVO.success(hazardReportService.statistics(tenantId, areaId));
    }

    /**
     * 查询单条详情。
     * <p>HTTP GET {@code /api/dual-prevention/hazards/{id}}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 资源主键 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/{id}")
    public ResponseVO<HazardReportVO> get(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(hazardReportService.getById(tenantId, id));
    }

    /**
     * 新建记录。
     * <p>HTTP POST {@code /api/dual-prevention/hazards}</p>
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping
    public ResponseVO<HazardReportVO> create(@Valid @RequestBody HazardReportRequest request,
                                             @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                             @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                             @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(hazardReportService.create(request, operator(userId, username, operator)));
    }

    /**
     * 新增confirm或触发confirm相关动作。
     * <p>HTTP POST {@code /api/dual-prevention/hazards/{id}/confirm}</p>
     * @param id 资源主键 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/{id}/confirm")
    public ResponseVO<HazardReportVO> confirm(@PathVariable Long id,
                                              @Valid @RequestBody HazardConfirmRequest request,
                                              @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                              @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                              @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(hazardReportService.confirm(id, request, operator(userId, username, operator)));
    }

    /**
     * 新增rectify或触发rectify相关动作。
     * <p>HTTP POST {@code /api/dual-prevention/hazards/{id}/rectify}</p>
     * @param id 资源主键 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/{id}/rectify")
    public ResponseVO<HazardReportVO> rectify(@PathVariable Long id,
                                              @Valid @RequestBody HazardRectifyRequest request,
                                              @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                              @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                              @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(hazardReportService.rectify(id, request, operator(userId, username, operator)));
    }

    /**
     * 新增review或触发review相关动作。
     * <p>HTTP POST {@code /api/dual-prevention/hazards/{id}/review}</p>
     * @param id 资源主键 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/{id}/review")
    public ResponseVO<HazardReportVO> review(@PathVariable Long id,
                                             @Valid @RequestBody HazardReviewRequest request,
                                             @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                             @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                             @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(hazardReportService.review(id, request, operator(userId, username, operator)));
    }

    /**
     * 新增overdue check或触发overdue check相关动作。
     * <p>HTTP POST {@code /api/dual-prevention/hazards/overdue-check}</p>
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/overdue-check")
    public ResponseVO<HazardOverdueCheckVO> overdueCheck(@Valid @RequestBody HazardOverdueCheckRequest request) {
        return ResponseVO.success(hazardReportService.overdueCheck(request));
    }

    /**
     * 新增area open check或触发area open check相关动作。
     * <p>HTTP POST {@code /api/dual-prevention/hazards/area-open-check}</p>
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/area-open-check")
    public ResponseVO<HazardAreaOpenCheckVO> areaOpenCheck(@Valid @RequestBody HazardAreaOpenCheckRequest request) {
        return ResponseVO.success(hazardReportService.areaOpenCheck(request));
    }

    /**
     * 新增escalate或触发escalate相关动作。
     * <p>HTTP POST {@code /api/dual-prevention/hazards/{id}/escalate}</p>
     * @param id 资源主键 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
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
