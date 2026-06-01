package com.fgroupboss.ai.psm.operation.workpermit.lifting.controller;

import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.common.UserContextHeaders;
import com.fgroupboss.ai.psm.common.UserContextResolver;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.LiftingEquipmentCheckRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.LiftingTrialRecordRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.LiftingWorkDetailRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.HeightWorkFlowProgressVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.LiftingEquipmentCheckVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.LiftingPreCheckResultVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.LiftingTrialRecordVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.LiftingWorkDetailVO;
import com.fgroupboss.ai.psm.operation.workpermit.lifting.service.LiftingService;
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
 * 吊装作业专项接口。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/work-permits/{id}/lifting")
public class LiftingController {

    private final LiftingService liftingService;

    @GetMapping("/detail")
    public ResponseVO<LiftingWorkDetailVO> getDetail(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(liftingService.getDetail(tenantId, id));
    }

    @PutMapping("/detail")
    public ResponseVO<LiftingWorkDetailVO> saveDetail(@PathVariable Long id,
                                                      @Valid @RequestBody LiftingWorkDetailRequest request,
                                                      @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                                      @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                                      @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(liftingService.saveDetail(request.getTenantId(), id, request,
                operator(userId, username, operator)));
    }

    @GetMapping("/equipment-checks")
    public ResponseVO<List<LiftingEquipmentCheckVO>> listEquipmentChecks(@PathVariable Long id,
                                                                         @RequestParam Long tenantId) {
        return ResponseVO.success(liftingService.listEquipmentChecks(tenantId, id));
    }

    @PostMapping("/equipment-checks")
    public ResponseVO<LiftingEquipmentCheckVO> addEquipmentCheck(@PathVariable Long id,
                                                                 @Valid @RequestBody LiftingEquipmentCheckRequest request,
                                                                 @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                                                 @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                                                 @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(liftingService.addEquipmentCheck(request.getTenantId(), id, request,
                operator(userId, username, operator)));
    }

    @GetMapping("/trial-records")
    public ResponseVO<List<LiftingTrialRecordVO>> listTrialRecords(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(liftingService.listTrialRecords(tenantId, id));
    }

    @PostMapping("/trial-records")
    public ResponseVO<LiftingTrialRecordVO> addTrialRecord(@PathVariable Long id,
                                                           @Valid @RequestBody LiftingTrialRecordRequest request,
                                                           @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                                           @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                                           @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(liftingService.addTrialRecord(request.getTenantId(), id, request,
                operator(userId, username, operator)));
    }

    @PostMapping("/pre-check")
    public ResponseVO<LiftingPreCheckResultVO> preCheck(@PathVariable Long id,
                                                        @RequestParam Long tenantId,
                                                        @RequestParam String checkPoint) {
        return ResponseVO.success(liftingService.preCheck(tenantId, id, checkPoint));
    }

    @GetMapping("/flow-progress")
    public ResponseVO<HeightWorkFlowProgressVO> flowProgress(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(liftingService.getFlowProgress(tenantId, id));
    }

    private String operator(String userId, String username, String fallback) {
        return UserContextResolver.operator(userId, username, fallback);
    }
}
