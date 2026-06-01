package com.fgroupboss.ai.psm.processsafety.pha.controller;

import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.processsafety.pha.model.dto.HazopDeviationRequest;
import com.fgroupboss.ai.psm.processsafety.pha.model.dto.HazopSubItemRequest;
import com.fgroupboss.ai.psm.processsafety.pha.model.vo.HazopCauseVO;
import com.fgroupboss.ai.psm.processsafety.pha.model.vo.HazopConsequenceVO;
import com.fgroupboss.ai.psm.processsafety.pha.model.vo.HazopDeviationVO;
import com.fgroupboss.ai.psm.processsafety.pha.model.vo.HazopSafeguardVO;
import com.fgroupboss.ai.psm.processsafety.pha.service.HazopDeviationService;
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
import java.util.List;

/**
 * HazopDeviation 模块 HTTP API。
 * <p>返回体均为 {@link com.fgroupboss.ai.psm.common.ResponseVO}；写操作需透传租户与操作人上下文。</p>
 */
@RestController
@RequiredArgsConstructor
public class HazopDeviationController {

    private final HazopDeviationService hazopDeviationService;

    /**
     * 查询deviations。
     * <p>HTTP GET {@code /api/pha/nodes/{nodeId}/deviations}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param nodeId PHA 节点 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 列表数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/api/pha/nodes/{nodeId}/deviations")
    public ResponseVO<List<HazopDeviationVO>> listByNode(@PathVariable Long nodeId, @RequestParam Long tenantId) {
        return ResponseVO.success(hazopDeviationService.listByNode(tenantId, nodeId));
    }

    /**
     * 新建记录。
     * <p>HTTP POST {@code /api/pha/nodes/{nodeId}/deviations}</p>
     * @param nodeId PHA 节点 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/api/pha/nodes/{nodeId}/deviations")
    public ResponseVO<HazopDeviationVO> create(@PathVariable Long nodeId,
                                               @Valid @RequestBody HazopDeviationRequest request,
                                               @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(hazopDeviationService.create(nodeId, request, operator));
    }

    /**
     * 查询causes。
     * <p>HTTP GET {@code /api/pha/deviations/{deviationId}/causes}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param deviationId deviation ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 列表数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/api/pha/deviations/{deviationId}/causes")
    public ResponseVO<List<HazopCauseVO>> listCauses(@PathVariable Long deviationId, @RequestParam Long tenantId) {
        return ResponseVO.success(hazopDeviationService.listCauses(tenantId, deviationId));
    }

    /**
     * 新增causes或触发causes相关动作。
     * <p>HTTP POST {@code /api/pha/deviations/{deviationId}/causes}</p>
     * @param deviationId deviation ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/api/pha/deviations/{deviationId}/causes")
    public ResponseVO<HazopCauseVO> addCause(@PathVariable Long deviationId,
                                             @Valid @RequestBody HazopSubItemRequest request,
                                             @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(hazopDeviationService.addCause(deviationId, request, operator));
    }

    /**
     * 查询consequences。
     * <p>HTTP GET {@code /api/pha/deviations/{deviationId}/consequences}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param deviationId deviation ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 列表数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/api/pha/deviations/{deviationId}/consequences")
    public ResponseVO<List<HazopConsequenceVO>> listConsequences(@PathVariable Long deviationId, @RequestParam Long tenantId) {
        return ResponseVO.success(hazopDeviationService.listConsequences(tenantId, deviationId));
    }

    /**
     * 新增consequences或触发consequences相关动作。
     * <p>HTTP POST {@code /api/pha/deviations/{deviationId}/consequences}</p>
     * @param deviationId deviation ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/api/pha/deviations/{deviationId}/consequences")
    public ResponseVO<HazopConsequenceVO> addConsequence(@PathVariable Long deviationId,
                                                         @Valid @RequestBody HazopSubItemRequest request,
                                                         @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(hazopDeviationService.addConsequence(deviationId, request, operator));
    }

    /**
     * 查询safeguards。
     * <p>HTTP GET {@code /api/pha/deviations/{deviationId}/safeguards}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param deviationId deviation ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 列表数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/api/pha/deviations/{deviationId}/safeguards")
    public ResponseVO<List<HazopSafeguardVO>> listSafeguards(@PathVariable Long deviationId, @RequestParam Long tenantId) {
        return ResponseVO.success(hazopDeviationService.listSafeguards(tenantId, deviationId));
    }

    /**
     * 新增safeguards或触发safeguards相关动作。
     * <p>HTTP POST {@code /api/pha/deviations/{deviationId}/safeguards}</p>
     * @param deviationId deviation ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/api/pha/deviations/{deviationId}/safeguards")
    public ResponseVO<HazopSafeguardVO> addSafeguard(@PathVariable Long deviationId,
                                                     @Valid @RequestBody HazopSubItemRequest request,
                                                     @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(hazopDeviationService.addSafeguard(deviationId, request, operator));
    }
}
