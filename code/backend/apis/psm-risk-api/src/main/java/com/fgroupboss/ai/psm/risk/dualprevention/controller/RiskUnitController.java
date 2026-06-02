package com.fgroupboss.ai.psm.risk.dualprevention.controller;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.common.LoginContext;
import com.fgroupboss.ai.psm.common.UserContext;
import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.common.UserContextResolver;
import com.fgroupboss.ai.psm.risk.dualprevention.model.dto.RiskEventRequest;
import com.fgroupboss.ai.psm.risk.dualprevention.model.dto.RiskUnitRequest;
import com.fgroupboss.ai.psm.risk.dualprevention.model.vo.RiskColorStatVO;
import com.fgroupboss.ai.psm.risk.dualprevention.model.vo.RiskEventVO;
import com.fgroupboss.ai.psm.risk.dualprevention.model.vo.RiskUnitTreeNodeVO;
import com.fgroupboss.ai.psm.risk.dualprevention.model.vo.RiskUnitVO;
import com.fgroupboss.ai.psm.risk.dualprevention.service.RiskUnitService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * RiskUnit 模块 HTTP API。
 * <p>双重预防机制：风险单元、事件与管控措施。</p>
 * <p>基础路径：{@code /api/dual-prevention/risk-units}</p>
 * <p>返回体均为 {@link com.fgroupboss.ai.psm.common.ResponseVO}；写操作需透传租户与操作人上下文。</p>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dual-prevention/risk-units")
public class RiskUnitController {

    private final RiskUnitService riskUnitService;

    /**
     * 分页查询列表。
     * <p>HTTP GET {@code /api/dual-prevention/risk-units}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param tenantId 租户 ID，多租户隔离必填
     * @param keyword 模糊搜索关键字
     * @param status 业务状态筛选
     * @param areaId 区域 ID
     * @param pageNo 页码，从 1 开始
     * @param pageSize 每页条数，默认 20
     * @return 分页数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping
    public ResponseVO<PageResult<RiskUnitVO>> page(@LoginContext UserContext loginContext,
                                                                                                     @RequestParam(required = false) String keyword,
                                                   @RequestParam(required = false) String status,
                                                   @RequestParam(required = false) Long areaId,
                                                   @RequestParam(defaultValue = "1") int pageNo,
                                                   @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseVO.success(riskUnitService.page(loginContext.getTenantId(), keyword, status, areaId, pageNo, pageSize));
    }

    /**
     * 查询tree。
     * <p>HTTP GET {@code /api/dual-prevention/risk-units/tree}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param tenantId 租户 ID，多租户隔离必填
     * @param areaId 区域 ID
     * @return 列表数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/tree")
    public ResponseVO<List<RiskUnitTreeNodeVO>> tree(@LoginContext UserContext loginContext,
                                        @RequestParam(required = false) Long areaId) {
        return ResponseVO.success(riskUnitService.tree(loginContext.getTenantId(), areaId));
    }

    /**
     * 查询color map。
     * <p>HTTP GET {@code /api/dual-prevention/risk-units/color-map}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param tenantId 租户 ID，多租户隔离必填
     * @param areaId 区域 ID
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/color-map")
    public ResponseVO<Map<String, Long>> colorMap(@LoginContext UserContext loginContext,
                                        @RequestParam(required = false) Long areaId) {
        return ResponseVO.success(riskUnitService.colorMap(loginContext.getTenantId(), areaId));
    }

    /**
     * 查询color stats。
     * <p>HTTP GET {@code /api/dual-prevention/risk-units/color-stats}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param tenantId 租户 ID，多租户隔离必填
     * @param areaId 区域 ID
     * @return 列表数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/color-stats")
    public ResponseVO<List<RiskColorStatVO>> colorStats(@LoginContext UserContext loginContext,
                                        @RequestParam(required = false) Long areaId) {
        return ResponseVO.success(riskUnitService.colorStats(loginContext.getTenantId(), areaId));
    }

    /**
     * 查询单条详情。
     * <p>HTTP GET {@code /api/dual-prevention/risk-units/{id}}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 资源主键 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/{id}")
    public ResponseVO<RiskUnitVO> get(@LoginContext UserContext loginContext,
                                        @PathVariable Long id) {
        return ResponseVO.success(riskUnitService.getById(loginContext.getTenantId(), id));
    }

    /**
     * 新建记录。
     * <p>HTTP POST {@code /api/dual-prevention/risk-units}</p>
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping
    public ResponseVO<RiskUnitVO> create(@LoginContext UserContext loginContext,
                                                  @Valid @RequestBody RiskUnitRequest request,
                                                                                                                           @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        request.setTenantId(loginContext.getTenantId());
        return ResponseVO.success(riskUnitService.create(request, UserContextResolver.operator(loginContext, operator)));
    }

    /**
     * 更新记录。
     * <p>HTTP PUT {@code /api/dual-prevention/risk-units/{id}}</p>
     * @param id 资源主键 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PutMapping("/{id}")
    public ResponseVO<RiskUnitVO> update(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                         @Valid @RequestBody RiskUnitRequest request,
                                                                                                                           @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        request.setTenantId(loginContext.getTenantId());
        return ResponseVO.success(riskUnitService.update(id, request, UserContextResolver.operator(loginContext, operator)));
    }

    /**
     * 删除记录。
     * <p>HTTP DELETE {@code /api/dual-prevention/risk-units/{id}}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 资源主键 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 无业务载荷（成功即可），统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @DeleteMapping("/{id}")
    public ResponseVO<Void> delete(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                                                                                                                            @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        riskUnitService.delete(loginContext.getTenantId(), id, UserContextResolver.operator(loginContext, operator));
        return ResponseVO.success(null);
    }

    /**
     * 查询events。
     * <p>HTTP GET {@code /api/dual-prevention/risk-units/{id}/events}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 资源主键 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 列表数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/{id}/events")
    public ResponseVO<List<RiskEventVO>> listEvents(@LoginContext UserContext loginContext,
                                        @PathVariable Long id) {
        return ResponseVO.success(riskUnitService.listEvents(loginContext.getTenantId(), id));
    }

    /**
     * 新增events或触发events相关动作。
     * <p>HTTP POST {@code /api/dual-prevention/risk-units/{id}/events}</p>
     * @param id 资源主键 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/{id}/events")
    public ResponseVO<RiskEventVO> createEvent(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                               @Valid @RequestBody RiskEventRequest request,
                                                                                                                                             @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        request.setTenantId(loginContext.getTenantId());
        return ResponseVO.success(riskUnitService.createEvent(id, request, UserContextResolver.operator(loginContext, operator)));
    }

}
