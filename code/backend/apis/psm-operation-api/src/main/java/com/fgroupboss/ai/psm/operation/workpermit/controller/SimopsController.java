package com.fgroupboss.ai.psm.operation.workpermit.controller;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.common.LoginContext;
import com.fgroupboss.ai.psm.common.UserContext;
import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.common.UserContextResolver;
import com.fgroupboss.ai.psm.operation.workpermit.model.dto.SimopsConflictRuleRequest;
import com.fgroupboss.ai.psm.operation.workpermit.model.dto.SimopsCoordinateRequest;
import com.fgroupboss.ai.psm.operation.workpermit.model.dto.SimopsScanRequest;
import com.fgroupboss.ai.psm.operation.workpermit.model.vo.SimopsConflictRuleVO;
import com.fgroupboss.ai.psm.operation.workpermit.model.vo.SimopsCoordinationRecordVO;
import com.fgroupboss.ai.psm.operation.workpermit.model.vo.SimopsScanResultVO;
import com.fgroupboss.ai.psm.operation.workpermit.model.vo.SimopsStatisticsVO;
import com.fgroupboss.ai.psm.operation.workpermit.service.SimopsService;
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
 * Simops 模块 HTTP API。
 * <p>基础路径：{@code /api/simops}</p>
 * <p>返回体均为 {@link com.fgroupboss.ai.psm.common.ResponseVO}；写操作需透传租户与操作人上下文。</p>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/simops")
public class SimopsController {

    private final SimopsService simopsService;

    /**
     * 查询rules。
     * <p>HTTP GET {@code /api/simops/rules}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param tenantId 租户 ID，多租户隔离必填
     * @param enabledOnly enabledOnly 参数
     * @return 列表数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/rules")
    public ResponseVO<List<SimopsConflictRuleVO>> listRules(@LoginContext UserContext loginContext,
                                        @RequestParam(required = false) Boolean enabledOnly) {
        return ResponseVO.success(simopsService.listRules(loginContext.getTenantId(), enabledOnly));
    }

    /**
     * 新增rules或触发rules相关动作。
     * <p>HTTP POST {@code /api/simops/rules}</p>
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/rules")
    public ResponseVO<SimopsConflictRuleVO> createRule(@Valid @RequestBody SimopsConflictRuleRequest request) {
        return ResponseVO.success(simopsService.createRule(request));
    }

    /**
     * 更新rules。
     * <p>HTTP PUT {@code /api/simops/rules/{id}}</p>
     * @param id 资源主键 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PutMapping("/rules/{id}")
    public ResponseVO<SimopsConflictRuleVO> updateRule(@PathVariable Long id,
                                                       @Valid @RequestBody SimopsConflictRuleRequest request) {
        return ResponseVO.success(simopsService.updateRule(id, request));
    }

    /**
     * 删除rules。
     * <p>HTTP DELETE {@code /api/simops/rules/{id}}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 资源主键 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 无业务载荷（成功即可），统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @DeleteMapping("/rules/{id}")
    public ResponseVO<Void> deleteRule(@LoginContext UserContext loginContext,
                                        @PathVariable Long id) {
        simopsService.deleteRule(loginContext.getTenantId(), id);
        return ResponseVO.success(null);
    }

    /**
     * 新增scan或触发scan相关动作。
     * <p>HTTP POST {@code /api/simops/scan}</p>
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/scan")
    public ResponseVO<SimopsScanResultVO> scan(@Valid @RequestBody SimopsScanRequest request) {
        return ResponseVO.success(simopsService.scan(request));
    }

    /**
     * 查询conflicts。
     * <p>HTTP GET {@code /api/simops/conflicts}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param tenantId 租户 ID，多租户隔离必填
     * @param workPermitId workPermit ID
     * @param scanStage scanStage 参数
     * @param pageNo 页码，从 1 开始
     * @param pageSize 每页条数，默认 20
     * @return 分页数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/conflicts")
    public ResponseVO<PageResult<SimopsScanResultVO>> listConflicts(@LoginContext UserContext loginContext,
                                        @RequestParam(required = false) Long workPermitId,
                                                                    @RequestParam(required = false) String scanStage,
                                                                    @RequestParam(defaultValue = "1") int pageNo,
                                                                    @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseVO.success(simopsService.listConflicts(loginContext.getTenantId(), workPermitId, scanStage, pageNo, pageSize));
    }

    /**
     * 新增coordinate或触发coordinate相关动作。
     * <p>HTTP POST {@code /api/simops/conflicts/{id}/coordinate}</p>
     * @param id 资源主键 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/conflicts/{id}/coordinate")
    public ResponseVO<SimopsCoordinationRecordVO> coordinate(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                                           @Valid @RequestBody SimopsCoordinateRequest request,
                                                                                                                                                                                 @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        request.setTenantId(loginContext.getTenantId());
        return ResponseVO.success(simopsService.coordinate(id, request,
                UserContextResolver.operator(loginContext, operator)));
    }

    /**
     * 查询statistics。
     * <p>HTTP GET {@code /api/simops/statistics}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/statistics")
    public ResponseVO<SimopsStatisticsVO> statistics(@LoginContext UserContext loginContext) {
        return ResponseVO.success(simopsService.statistics(loginContext.getTenantId()));
    }

}
