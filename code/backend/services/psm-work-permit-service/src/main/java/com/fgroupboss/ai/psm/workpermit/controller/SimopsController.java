package com.fgroupboss.ai.psm.workpermit.controller;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.common.UserContextHeaders;
import com.fgroupboss.ai.psm.common.UserContextResolver;
import com.fgroupboss.ai.psm.workpermit.model.dto.SimopsConflictRuleRequest;
import com.fgroupboss.ai.psm.workpermit.model.dto.SimopsCoordinateRequest;
import com.fgroupboss.ai.psm.workpermit.model.dto.SimopsScanRequest;
import com.fgroupboss.ai.psm.workpermit.model.vo.SimopsConflictRuleVO;
import com.fgroupboss.ai.psm.workpermit.model.vo.SimopsCoordinationRecordVO;
import com.fgroupboss.ai.psm.workpermit.model.vo.SimopsScanResultVO;
import com.fgroupboss.ai.psm.workpermit.model.vo.SimopsStatisticsVO;
import com.fgroupboss.ai.psm.workpermit.service.SimopsService;
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
 * SIMOPS 交叉作业接口。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/simops")
public class SimopsController {

    private final SimopsService simopsService;

    @GetMapping("/rules")
    public ResponseVO<List<SimopsConflictRuleVO>> listRules(@RequestParam Long tenantId,
                                                           @RequestParam(required = false) Boolean enabledOnly) {
        return ResponseVO.success(simopsService.listRules(tenantId, enabledOnly));
    }

    @PostMapping("/rules")
    public ResponseVO<SimopsConflictRuleVO> createRule(@Valid @RequestBody SimopsConflictRuleRequest request) {
        return ResponseVO.success(simopsService.createRule(request));
    }

    @PutMapping("/rules/{id}")
    public ResponseVO<SimopsConflictRuleVO> updateRule(@PathVariable Long id,
                                                       @Valid @RequestBody SimopsConflictRuleRequest request) {
        return ResponseVO.success(simopsService.updateRule(id, request));
    }

    @DeleteMapping("/rules/{id}")
    public ResponseVO<Void> deleteRule(@PathVariable Long id, @RequestParam Long tenantId) {
        simopsService.deleteRule(tenantId, id);
        return ResponseVO.success(null);
    }

    @PostMapping("/scan")
    public ResponseVO<SimopsScanResultVO> scan(@Valid @RequestBody SimopsScanRequest request) {
        return ResponseVO.success(simopsService.scan(request));
    }

    @GetMapping("/conflicts")
    public ResponseVO<PageResult<SimopsScanResultVO>> listConflicts(@RequestParam Long tenantId,
                                                                    @RequestParam(required = false) Long workPermitId,
                                                                    @RequestParam(required = false) String scanStage,
                                                                    @RequestParam(defaultValue = "1") int pageNo,
                                                                    @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseVO.success(simopsService.listConflicts(tenantId, workPermitId, scanStage, pageNo, pageSize));
    }

    @PostMapping("/conflicts/{id}/coordinate")
    public ResponseVO<SimopsCoordinationRecordVO> coordinate(@PathVariable Long id,
                                                           @Valid @RequestBody SimopsCoordinateRequest request,
                                                           @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                                           @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                                           @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(simopsService.coordinate(id, request,
                operator(userId, username, operator)));
    }

    @GetMapping("/statistics")
    public ResponseVO<SimopsStatisticsVO> statistics(@RequestParam Long tenantId) {
        return ResponseVO.success(simopsService.statistics(tenantId));
    }

    private String operator(String userId, String username, String fallback) {
        return UserContextResolver.operator(userId, username, fallback);
    }
}
