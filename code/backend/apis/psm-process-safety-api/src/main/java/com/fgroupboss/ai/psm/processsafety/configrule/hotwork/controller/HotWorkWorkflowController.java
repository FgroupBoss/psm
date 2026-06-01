package com.fgroupboss.ai.psm.processsafety.configrule.hotwork.controller;

import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.common.UserContextHeaders;
import com.fgroupboss.ai.psm.common.UserContextResolver;
import com.fgroupboss.ai.psm.processsafety.configrule.hotwork.model.dto.HotWorkApproverResolveDTO;
import com.fgroupboss.ai.psm.processsafety.configrule.hotwork.model.dto.HotWorkWorkflowQueryDTO;
import com.fgroupboss.ai.psm.processsafety.configrule.hotwork.model.dto.HotWorkWorkflowSnapshotDTO;
import com.fgroupboss.ai.psm.processsafety.configrule.hotwork.model.dto.HotWorkWorkflowTemplateRequest;
import com.fgroupboss.ai.psm.processsafety.configrule.hotwork.model.vo.HotWorkApproverResolveResultVO;
import com.fgroupboss.ai.psm.processsafety.configrule.hotwork.model.vo.HotWorkWorkflowDetailVO;
import com.fgroupboss.ai.psm.processsafety.configrule.hotwork.model.vo.HotWorkWorkflowSummaryVO;
import com.fgroupboss.ai.psm.processsafety.configrule.hotwork.service.HotWorkWorkflowService;
import lombok.RequiredArgsConstructor;
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
 * 动火审批流配置接口。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/config/hot-work/workflows")
public class HotWorkWorkflowController {

    private final HotWorkWorkflowService hotWorkWorkflowService;

    @PostMapping
    public ResponseVO<HotWorkWorkflowDetailVO> create(@Valid @RequestBody HotWorkWorkflowTemplateRequest request,
                                                      @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                                      @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                                      @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(hotWorkWorkflowService.create(request, operator(userId, username, operator)));
    }

    @PutMapping("/{id}")
    public ResponseVO<HotWorkWorkflowDetailVO> update(@PathVariable Long id,
                                                      @Valid @RequestBody HotWorkWorkflowTemplateRequest request,
                                                      @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                                      @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                                      @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(hotWorkWorkflowService.update(id, request, operator(userId, username, operator)));
    }

    @GetMapping("/{id}")
    public ResponseVO<HotWorkWorkflowDetailVO> get(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(hotWorkWorkflowService.get(tenantId, id));
    }

    @GetMapping("/available")
    public ResponseVO<List<HotWorkWorkflowSummaryVO>> available(@RequestParam Long tenantId,
                                                              @RequestParam(required = false) String hotWorkLevel,
                                                              @RequestParam(required = false) Long areaId,
                                                              @RequestParam(required = false) String status) {
        HotWorkWorkflowQueryDTO query = new HotWorkWorkflowQueryDTO();
        query.setTenantId(tenantId);
        query.setHotWorkLevel(hotWorkLevel);
        query.setAreaId(areaId);
        query.setStatus(status);
        return ResponseVO.success(hotWorkWorkflowService.listAvailable(query));
    }

    @GetMapping("/{id}/snapshot")
    public ResponseVO<HotWorkWorkflowSnapshotDTO> snapshot(@PathVariable Long id,
                                                           @RequestParam Long tenantId,
                                                           @RequestParam(required = false) Integer versionNo) {
        return ResponseVO.success(hotWorkWorkflowService.getSnapshot(tenantId, id, versionNo));
    }

    @PostMapping("/resolve-approvers")
    public ResponseVO<HotWorkApproverResolveResultVO> resolveApprovers(@RequestBody HotWorkApproverResolveDTO request) {
        return ResponseVO.success(hotWorkWorkflowService.resolveApprovers(request));
    }

    @PostMapping("/{id}/publish")
    public ResponseVO<Void> publish(@PathVariable Long id,
                                    @RequestParam Long tenantId,
                                    @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                    @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                    @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        hotWorkWorkflowService.publish(tenantId, id, operator(userId, username, operator));
        return ResponseVO.success();
    }

    @PostMapping("/{id}/disable")
    public ResponseVO<Void> disable(@PathVariable Long id,
                                    @RequestParam Long tenantId,
                                    @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                    @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                    @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        hotWorkWorkflowService.disable(tenantId, id, operator(userId, username, operator));
        return ResponseVO.success();
    }

    private String operator(String userId, String username, String fallback) {
        return UserContextResolver.operator(userId, username, fallback);
    }
}
