package com.fgroupboss.ai.psm.risk.dualprevention.controller;

import com.fgroupboss.ai.psm.common.LoginContext;
import com.fgroupboss.ai.psm.common.UserContext;
import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.common.UserContextResolver;
import com.fgroupboss.ai.psm.risk.dualprevention.model.dto.ControlMeasureRequest;
import com.fgroupboss.ai.psm.risk.dualprevention.model.dto.RiskEventRequest;
import com.fgroupboss.ai.psm.risk.dualprevention.model.vo.ControlMeasureVO;
import com.fgroupboss.ai.psm.risk.dualprevention.model.vo.RiskEventVO;
import com.fgroupboss.ai.psm.risk.dualprevention.service.RiskEventService;
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

/**
 * RiskEvent 模块 HTTP API。
 * <p>双重预防机制：风险单元、事件与管控措施。</p>
 * <p>基础路径：{@code /api/dual-prevention/risk-events}</p>
 * <p>返回体均为 {@link com.fgroupboss.ai.psm.common.ResponseVO}；写操作需透传租户与操作人上下文。</p>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dual-prevention/risk-events")
public class RiskEventController {

    private final RiskEventService riskEventService;

    /**
     * 更新记录。
     * <p>HTTP PUT {@code /api/dual-prevention/risk-events/{id}}</p>
     * @param id 资源主键 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PutMapping("/{id}")
    public ResponseVO<RiskEventVO> update(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                          @Valid @RequestBody RiskEventRequest request,
                                                                                                                              @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        request.setTenantId(loginContext.getTenantId());
        return ResponseVO.success(riskEventService.update(id, request, UserContextResolver.operator(loginContext, operator)));
    }

    /**
     * 删除记录。
     * <p>HTTP DELETE {@code /api/dual-prevention/risk-events/{id}}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 资源主键 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 无业务载荷（成功即可），统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @DeleteMapping("/{id}")
    public ResponseVO<Void> delete(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                                                                                                                            @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        riskEventService.delete(loginContext.getTenantId(), id, UserContextResolver.operator(loginContext, operator));
        return ResponseVO.success(null);
    }

    /**
     * 查询measures。
     * <p>HTTP GET {@code /api/dual-prevention/risk-events/{id}/measures}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 资源主键 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 列表数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/{id}/measures")
    public ResponseVO<List<ControlMeasureVO>> listMeasures(@LoginContext UserContext loginContext,
                                        @PathVariable Long id) {
        return ResponseVO.success(riskEventService.listMeasures(loginContext.getTenantId(), id));
    }

    /**
     * 新增measures或触发measures相关动作。
     * <p>HTTP POST {@code /api/dual-prevention/risk-events/{id}/measures}</p>
     * @param id 资源主键 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/{id}/measures")
    public ResponseVO<ControlMeasureVO> createMeasure(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                                      @Valid @RequestBody ControlMeasureRequest request,
                                                                                                                                                                  @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        request.setTenantId(loginContext.getTenantId());
        return ResponseVO.success(riskEventService.createMeasure(id, request, UserContextResolver.operator(loginContext, operator)));
    }

}
