package com.fgroupboss.ai.psm.operation.workpermit.heightwork.controller;

import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.common.UserContextHeaders;
import com.fgroupboss.ai.psm.common.UserContextResolver;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.HeightWorkDetailRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.HeightWorkEnvironmentCheckRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.HeightWorkHazardFactorRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.HeightWorkProtectionCheckRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.HeightWorkDetailVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.HeightWorkEnvironmentCheckVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.HeightWorkFlowProgressVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.HeightWorkHazardFactorVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.HeightWorkPreCheckResultVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.HeightWorkProtectionCheckVO;
import com.fgroupboss.ai.psm.operation.workpermit.heightwork.service.HeightWorkService;
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
 * 高处作业专项接口。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/work-permits/{id}/height-work")
public class HeightWorkController {

    private final HeightWorkService heightWorkService;

    @GetMapping("/detail")
    public ResponseVO<HeightWorkDetailVO> getDetail(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(heightWorkService.getDetail(tenantId, id));
    }

    @PutMapping("/detail")
    public ResponseVO<HeightWorkDetailVO> saveDetail(@PathVariable Long id,
                                                     @Valid @RequestBody HeightWorkDetailRequest request,
                                                     @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                                     @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                                     @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(heightWorkService.saveDetail(request.getTenantId(), id, request,
                operator(userId, username, operator)));
    }

    @GetMapping("/hazard-factors")
    public ResponseVO<List<HeightWorkHazardFactorVO>> listHazardFactors(@PathVariable Long id,
                                                                        @RequestParam Long tenantId) {
        return ResponseVO.success(heightWorkService.listHazardFactors(tenantId, id));
    }

    @PostMapping("/hazard-factors")
    public ResponseVO<HeightWorkHazardFactorVO> addHazardFactor(@PathVariable Long id,
                                                                  @Valid @RequestBody HeightWorkHazardFactorRequest request,
                                                                  @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                                                  @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                                                  @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(heightWorkService.addHazardFactor(request.getTenantId(), id, request,
                operator(userId, username, operator)));
    }

    @PostMapping("/hazard-factors/{factorId}/confirm")
    public ResponseVO<HeightWorkHazardFactorVO> confirmHazardFactor(@PathVariable Long id,
                                                                    @PathVariable Long factorId,
                                                                    @RequestParam Long tenantId,
                                                                    @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                                                    @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                                                    @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(heightWorkService.confirmHazardFactor(tenantId, id, factorId,
                operator(userId, username, operator)));
    }

    @GetMapping("/protection-checks")
    public ResponseVO<List<HeightWorkProtectionCheckVO>> listProtectionChecks(@PathVariable Long id,
                                                                              @RequestParam Long tenantId,
                                                                              @RequestParam(required = false) String checkStage) {
        return ResponseVO.success(heightWorkService.listProtectionChecks(tenantId, id, checkStage));
    }

    @PostMapping("/protection-checks")
    public ResponseVO<HeightWorkProtectionCheckVO> addProtectionCheck(@PathVariable Long id,
                                                                      @Valid @RequestBody HeightWorkProtectionCheckRequest request,
                                                                      @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                                                      @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                                                      @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(heightWorkService.addProtectionCheck(request.getTenantId(), id, request,
                operator(userId, username, operator)));
    }

    @GetMapping("/environment-checks")
    public ResponseVO<List<HeightWorkEnvironmentCheckVO>> listEnvironmentChecks(@PathVariable Long id,
                                                                                @RequestParam Long tenantId,
                                                                                @RequestParam(required = false) String checkStage) {
        return ResponseVO.success(heightWorkService.listEnvironmentChecks(tenantId, id, checkStage));
    }

    @PostMapping("/environment-checks")
    public ResponseVO<HeightWorkEnvironmentCheckVO> addEnvironmentCheck(@PathVariable Long id,
                                                                        @Valid @RequestBody HeightWorkEnvironmentCheckRequest request,
                                                                        @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                                                        @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                                                        @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(heightWorkService.addEnvironmentCheck(request.getTenantId(), id, request,
                operator(userId, username, operator)));
    }

    @PostMapping("/recalculate")
    public ResponseVO<HeightWorkDetailVO> recalculate(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(heightWorkService.recalculate(tenantId, id));
    }

    @PostMapping("/pre-check")
    public ResponseVO<HeightWorkPreCheckResultVO> preCheck(@PathVariable Long id,
                                                           @RequestParam Long tenantId,
                                                           @RequestParam String checkPoint) {
        return ResponseVO.success(heightWorkService.preCheck(tenantId, id, checkPoint));
    }

    @GetMapping("/flow-progress")
    public ResponseVO<HeightWorkFlowProgressVO> flowProgress(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(heightWorkService.getFlowProgress(tenantId, id));
    }

    private String operator(String userId, String username, String fallback) {
        return UserContextResolver.operator(userId, username, fallback);
    }
}
