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

@RestController
@RequiredArgsConstructor
public class HazopDeviationController {

    private final HazopDeviationService hazopDeviationService;

    @GetMapping("/api/pha/nodes/{nodeId}/deviations")
    public ResponseVO<List<HazopDeviationVO>> listByNode(@PathVariable Long nodeId, @RequestParam Long tenantId) {
        return ResponseVO.success(hazopDeviationService.listByNode(tenantId, nodeId));
    }

    @PostMapping("/api/pha/nodes/{nodeId}/deviations")
    public ResponseVO<HazopDeviationVO> create(@PathVariable Long nodeId,
                                               @Valid @RequestBody HazopDeviationRequest request,
                                               @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(hazopDeviationService.create(nodeId, request, operator));
    }

    @GetMapping("/api/pha/deviations/{deviationId}/causes")
    public ResponseVO<List<HazopCauseVO>> listCauses(@PathVariable Long deviationId, @RequestParam Long tenantId) {
        return ResponseVO.success(hazopDeviationService.listCauses(tenantId, deviationId));
    }

    @PostMapping("/api/pha/deviations/{deviationId}/causes")
    public ResponseVO<HazopCauseVO> addCause(@PathVariable Long deviationId,
                                             @Valid @RequestBody HazopSubItemRequest request,
                                             @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(hazopDeviationService.addCause(deviationId, request, operator));
    }

    @GetMapping("/api/pha/deviations/{deviationId}/consequences")
    public ResponseVO<List<HazopConsequenceVO>> listConsequences(@PathVariable Long deviationId, @RequestParam Long tenantId) {
        return ResponseVO.success(hazopDeviationService.listConsequences(tenantId, deviationId));
    }

    @PostMapping("/api/pha/deviations/{deviationId}/consequences")
    public ResponseVO<HazopConsequenceVO> addConsequence(@PathVariable Long deviationId,
                                                         @Valid @RequestBody HazopSubItemRequest request,
                                                         @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(hazopDeviationService.addConsequence(deviationId, request, operator));
    }

    @GetMapping("/api/pha/deviations/{deviationId}/safeguards")
    public ResponseVO<List<HazopSafeguardVO>> listSafeguards(@PathVariable Long deviationId, @RequestParam Long tenantId) {
        return ResponseVO.success(hazopDeviationService.listSafeguards(tenantId, deviationId));
    }

    @PostMapping("/api/pha/deviations/{deviationId}/safeguards")
    public ResponseVO<HazopSafeguardVO> addSafeguard(@PathVariable Long deviationId,
                                                     @Valid @RequestBody HazopSubItemRequest request,
                                                     @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(hazopDeviationService.addSafeguard(deviationId, request, operator));
    }
}
