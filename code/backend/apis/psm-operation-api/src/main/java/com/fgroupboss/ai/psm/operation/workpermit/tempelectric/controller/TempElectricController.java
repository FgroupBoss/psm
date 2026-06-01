package com.fgroupboss.ai.psm.operation.workpermit.tempelectric.controller;

import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.common.UserContextHeaders;
import com.fgroupboss.ai.psm.common.UserContextResolver;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.TempElectricDetailRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.TempElectricFacilityRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.TempElectricInspectionRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.HeightWorkFlowProgressVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.TempElectricDetailVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.TempElectricFacilityVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.TempElectricInspectionVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.TempElectricPreCheckResultVO;
import com.fgroupboss.ai.psm.operation.workpermit.tempelectric.service.TempElectricService;
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
 * 临时用电专项接口。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/work-permits/{id}/temporary-electric")
public class TempElectricController {

    private final TempElectricService tempElectricService;

    @GetMapping("/detail")
    public ResponseVO<TempElectricDetailVO> getDetail(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(tempElectricService.getDetail(tenantId, id));
    }

    @PutMapping("/detail")
    public ResponseVO<TempElectricDetailVO> saveDetail(@PathVariable Long id,
                                                       @Valid @RequestBody TempElectricDetailRequest request,
                                                       @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                                       @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                                       @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(tempElectricService.saveDetail(request.getTenantId(), id, request,
                operator(userId, username, operator)));
    }

    @GetMapping("/facilities")
    public ResponseVO<List<TempElectricFacilityVO>> listFacilities(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(tempElectricService.listFacilities(tenantId, id));
    }

    @PostMapping("/facilities")
    public ResponseVO<TempElectricFacilityVO> addFacility(@PathVariable Long id,
                                                        @Valid @RequestBody TempElectricFacilityRequest request,
                                                        @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                                        @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                                        @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(tempElectricService.addFacility(request.getTenantId(), id, request,
                operator(userId, username, operator)));
    }

    @GetMapping("/inspections")
    public ResponseVO<List<TempElectricInspectionVO>> listInspections(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(tempElectricService.listInspections(tenantId, id));
    }

    @PostMapping("/inspections")
    public ResponseVO<TempElectricInspectionVO> addInspection(@PathVariable Long id,
                                                              @Valid @RequestBody TempElectricInspectionRequest request,
                                                              @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                                              @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                                              @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(tempElectricService.addInspection(request.getTenantId(), id, request,
                operator(userId, username, operator)));
    }

    @PostMapping("/pre-check")
    public ResponseVO<TempElectricPreCheckResultVO> preCheck(@PathVariable Long id,
                                                             @RequestParam Long tenantId,
                                                             @RequestParam String checkPoint) {
        return ResponseVO.success(tempElectricService.preCheck(tenantId, id, checkPoint));
    }

    @GetMapping("/flow-progress")
    public ResponseVO<HeightWorkFlowProgressVO> flowProgress(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(tempElectricService.getFlowProgress(tenantId, id));
    }

    private String operator(String userId, String username, String fallback) {
        return UserContextResolver.operator(userId, username, fallback);
    }
}
