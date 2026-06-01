package com.fgroupboss.ai.psm.operation.workpermit.confinedspace.controller;

import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.common.UserContextHeaders;
import com.fgroupboss.ai.psm.common.UserContextResolver;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.ConfinedSpaceDetailRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.ConfinedSpaceEntryRecordRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.ConfinedSpaceRescuePlanRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.ConfinedSpaceDetailVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.ConfinedSpaceEntryRecordVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.ConfinedSpacePreCheckResultVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.ConfinedSpaceRescuePlanVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.HeightWorkFlowProgressVO;
import com.fgroupboss.ai.psm.operation.workpermit.confinedspace.service.ConfinedSpaceService;
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
 * 受限空间专项接口。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/work-permits/{id}/confined-space")
public class ConfinedSpaceController {

    private final ConfinedSpaceService confinedSpaceService;

    @GetMapping("/detail")
    public ResponseVO<ConfinedSpaceDetailVO> getDetail(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(confinedSpaceService.getDetail(tenantId, id));
    }

    @PutMapping("/detail")
    public ResponseVO<ConfinedSpaceDetailVO> saveDetail(@PathVariable Long id,
                                                        @Valid @RequestBody ConfinedSpaceDetailRequest request,
                                                        @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                                        @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                                        @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(confinedSpaceService.saveDetail(request.getTenantId(), id, request,
                operator(userId, username, operator)));
    }

    @GetMapping("/entries")
    public ResponseVO<List<ConfinedSpaceEntryRecordVO>> listEntries(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(confinedSpaceService.listEntryRecords(tenantId, id));
    }

    @PostMapping("/entries")
    public ResponseVO<ConfinedSpaceEntryRecordVO> addEntry(@PathVariable Long id,
                                                           @Valid @RequestBody ConfinedSpaceEntryRecordRequest request,
                                                           @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                                           @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                                           @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(confinedSpaceService.addEntryRecord(request.getTenantId(), id, request,
                operator(userId, username, operator)));
    }

    @GetMapping("/rescue-plan")
    public ResponseVO<ConfinedSpaceRescuePlanVO> getRescuePlan(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(confinedSpaceService.getRescuePlan(tenantId, id));
    }

    @PutMapping("/rescue-plan")
    public ResponseVO<ConfinedSpaceRescuePlanVO> saveRescuePlan(@PathVariable Long id,
                                                                @Valid @RequestBody ConfinedSpaceRescuePlanRequest request,
                                                                @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                                                @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                                                @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(confinedSpaceService.saveRescuePlan(request.getTenantId(), id, request,
                operator(userId, username, operator)));
    }

    @PostMapping("/pre-check")
    public ResponseVO<ConfinedSpacePreCheckResultVO> preCheck(@PathVariable Long id,
                                                              @RequestParam Long tenantId,
                                                              @RequestParam String checkPoint) {
        return ResponseVO.success(confinedSpaceService.preCheck(tenantId, id, checkPoint));
    }

    @GetMapping("/flow-progress")
    public ResponseVO<HeightWorkFlowProgressVO> flowProgress(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(confinedSpaceService.getFlowProgress(tenantId, id));
    }

    private String operator(String userId, String username, String fallback) {
        return UserContextResolver.operator(userId, username, fallback);
    }
}
