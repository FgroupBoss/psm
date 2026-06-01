package com.fgroupboss.ai.psm.operation.workpermit.excavation.controller;

import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.common.UserContextHeaders;
import com.fgroupboss.ai.psm.common.UserContextResolver;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.ExcavationCountersignRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.ExcavationSiteCheckRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.ExcavationUndergroundFacilityRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.ExcavationWorkDetailRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.ExcavationCountersignVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.ExcavationFlowProgressVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.ExcavationPreCheckResultVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.ExcavationSiteCheckVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.ExcavationUndergroundFacilityVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.ExcavationWorkDetailVO;
import com.fgroupboss.ai.psm.operation.workpermit.excavation.service.ExcavationService;
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
 * 动土作业专项接口。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/work-permits/{id}/excavation")
public class ExcavationController {

    private final ExcavationService excavationService;

    @GetMapping("/detail")
    public ResponseVO<ExcavationWorkDetailVO> getDetail(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(excavationService.getDetail(tenantId, id));
    }

    @PutMapping("/detail")
    public ResponseVO<ExcavationWorkDetailVO> saveDetail(@PathVariable Long id,
                                                           @Valid @RequestBody ExcavationWorkDetailRequest request,
                                                           @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                                           @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                                           @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(excavationService.saveDetail(request.getTenantId(), id, request,
                operator(userId, username, operator)));
    }

    @GetMapping("/facilities")
    public ResponseVO<List<ExcavationUndergroundFacilityVO>> listFacilities(@PathVariable Long id,
                                                                             @RequestParam Long tenantId) {
        return ResponseVO.success(excavationService.listFacilities(tenantId, id));
    }

    @PostMapping("/facilities")
    public ResponseVO<ExcavationUndergroundFacilityVO> addFacility(@PathVariable Long id,
                                                                   @Valid @RequestBody ExcavationUndergroundFacilityRequest request,
                                                                   @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                                                   @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                                                   @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(excavationService.addFacility(request.getTenantId(), id, request,
                operator(userId, username, operator)));
    }

    @GetMapping("/countersigns")
    public ResponseVO<List<ExcavationCountersignVO>> listCountersigns(@PathVariable Long id,
                                                                      @RequestParam Long tenantId) {
        return ResponseVO.success(excavationService.listCountersigns(tenantId, id));
    }

    @PostMapping("/countersigns")
    public ResponseVO<ExcavationCountersignVO> addCountersign(@PathVariable Long id,
                                                              @Valid @RequestBody ExcavationCountersignRequest request,
                                                              @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                                              @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                                              @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(excavationService.addCountersign(request.getTenantId(), id, request,
                operator(userId, username, operator)));
    }

    @GetMapping("/site-checks")
    public ResponseVO<List<ExcavationSiteCheckVO>> listSiteChecks(@PathVariable Long id,
                                                                  @RequestParam Long tenantId,
                                                                  @RequestParam(required = false) String stage) {
        return ResponseVO.success(excavationService.listSiteChecks(tenantId, id, stage));
    }

    @PostMapping("/site-checks")
    public ResponseVO<ExcavationSiteCheckVO> addSiteCheck(@PathVariable Long id,
                                                          @Valid @RequestBody ExcavationSiteCheckRequest request,
                                                          @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                                          @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                                          @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(excavationService.addSiteCheck(request.getTenantId(), id, request,
                operator(userId, username, operator)));
    }

    @PostMapping("/pre-check")
    public ResponseVO<ExcavationPreCheckResultVO> preCheck(@PathVariable Long id,
                                                            @RequestParam Long tenantId,
                                                            @RequestParam String checkPoint) {
        return ResponseVO.success(excavationService.preCheck(tenantId, id, checkPoint));
    }

    @GetMapping("/flow-progress")
    public ResponseVO<ExcavationFlowProgressVO> flowProgress(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(excavationService.getFlowProgress(tenantId, id));
    }

    private String operator(String userId, String username, String fallback) {
        return UserContextResolver.operator(userId, username, fallback);
    }
}
