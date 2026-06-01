package com.fgroupboss.ai.psm.operation.workpermit.roadbreak.controller;

import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.common.UserContextHeaders;
import com.fgroupboss.ai.psm.common.UserContextResolver;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.RoadBreakDetailRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.RoadBreakSiteControlRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.RoadBreakTrafficPlanRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.RoadBreakDetailVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.RoadBreakFlowProgressVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.RoadBreakPreCheckResultVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.RoadBreakSiteControlVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.RoadBreakTrafficPlanVO;
import com.fgroupboss.ai.psm.operation.workpermit.roadbreak.service.RoadBreakService;
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
 * 断路作业专项接口。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/work-permits/{id}/road-break")
public class RoadBreakController {

    private final RoadBreakService roadBreakService;

    @GetMapping("/detail")
    public ResponseVO<RoadBreakDetailVO> getDetail(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(roadBreakService.getDetail(tenantId, id));
    }

    @PutMapping("/detail")
    public ResponseVO<RoadBreakDetailVO> saveDetail(@PathVariable Long id,
                                                    @Valid @RequestBody RoadBreakDetailRequest request,
                                                    @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                                    @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                                    @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(roadBreakService.saveDetail(request.getTenantId(), id, request,
                operator(userId, username, operator)));
    }

    @GetMapping("/traffic-plan")
    public ResponseVO<RoadBreakTrafficPlanVO> getTrafficPlan(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(roadBreakService.getTrafficPlan(tenantId, id));
    }

    @PutMapping("/traffic-plan")
    public ResponseVO<RoadBreakTrafficPlanVO> saveTrafficPlan(@PathVariable Long id,
                                                              @Valid @RequestBody RoadBreakTrafficPlanRequest request,
                                                              @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                                              @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                                              @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(roadBreakService.saveTrafficPlan(request.getTenantId(), id, request,
                operator(userId, username, operator)));
    }

    @GetMapping("/site-controls")
    public ResponseVO<List<RoadBreakSiteControlVO>> listSiteControls(@PathVariable Long id,
                                                                      @RequestParam Long tenantId) {
        return ResponseVO.success(roadBreakService.listSiteControls(tenantId, id));
    }

    @PostMapping("/site-controls")
    public ResponseVO<RoadBreakSiteControlVO> addSiteControl(@PathVariable Long id,
                                                               @Valid @RequestBody RoadBreakSiteControlRequest request,
                                                               @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                                               @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                                               @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(roadBreakService.addSiteControl(request.getTenantId(), id, request,
                operator(userId, username, operator)));
    }

    @PostMapping("/pre-check")
    public ResponseVO<RoadBreakPreCheckResultVO> preCheck(@PathVariable Long id,
                                                           @RequestParam Long tenantId,
                                                           @RequestParam String checkPoint) {
        return ResponseVO.success(roadBreakService.preCheck(tenantId, id, checkPoint));
    }

    @GetMapping("/flow-progress")
    public ResponseVO<RoadBreakFlowProgressVO> flowProgress(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(roadBreakService.getFlowProgress(tenantId, id));
    }

    private String operator(String userId, String username, String fallback) {
        return UserContextResolver.operator(userId, username, fallback);
    }
}
