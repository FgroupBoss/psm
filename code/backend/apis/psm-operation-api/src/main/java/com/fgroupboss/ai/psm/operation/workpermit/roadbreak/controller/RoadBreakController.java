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
 * <p>断路范围、交通组织方案及相关方确认。</p>
 * <p>基础路径：{@code /api/work-permits/{id}/road-break}</p>
 * <p>返回体均为 {@link com.fgroupboss.ai.psm.common.ResponseVO}；写操作需透传租户与操作人上下文。</p>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/work-permits/{id}/road-break")
public class RoadBreakController {

    private final RoadBreakService roadBreakService;

    /**
     * 查询专项详情。
     * <p>HTTP GET {@code /api/work-permits/{id}/road-break/detail}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 作业票 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/detail")
    public ResponseVO<RoadBreakDetailVO> getDetail(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(roadBreakService.getDetail(tenantId, id));
    }

    /**
     * 更新专项详情。
     * <p>HTTP PUT {@code /api/work-permits/{id}/road-break/detail}</p>
     * @param id 作业票 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PutMapping("/detail")
    public ResponseVO<RoadBreakDetailVO> saveDetail(@PathVariable Long id,
                                                    @Valid @RequestBody RoadBreakDetailRequest request,
                                                    @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                                    @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                                    @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(roadBreakService.saveDetail(request.getTenantId(), id, request,
                operator(userId, username, operator)));
    }

    /**
     * 查询交通组织方案。
     * <p>HTTP GET {@code /api/work-permits/{id}/road-break/traffic-plan}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 作业票 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/traffic-plan")
    public ResponseVO<RoadBreakTrafficPlanVO> getTrafficPlan(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(roadBreakService.getTrafficPlan(tenantId, id));
    }

    /**
     * 更新交通组织方案。
     * <p>HTTP PUT {@code /api/work-permits/{id}/road-break/traffic-plan}</p>
     * @param id 作业票 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PutMapping("/traffic-plan")
    public ResponseVO<RoadBreakTrafficPlanVO> saveTrafficPlan(@PathVariable Long id,
                                                              @Valid @RequestBody RoadBreakTrafficPlanRequest request,
                                                              @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                                              @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                                              @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(roadBreakService.saveTrafficPlan(request.getTenantId(), id, request,
                operator(userId, username, operator)));
    }

    /**
     * 查询site controls。
     * <p>HTTP GET {@code /api/work-permits/{id}/road-break/site-controls}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 作业票 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 列表数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/site-controls")
    public ResponseVO<List<RoadBreakSiteControlVO>> listSiteControls(@PathVariable Long id,
                                                                      @RequestParam Long tenantId) {
        return ResponseVO.success(roadBreakService.listSiteControls(tenantId, id));
    }

    /**
     * 新增site controls或触发site controls相关动作。
     * <p>HTTP POST {@code /api/work-permits/{id}/road-break/site-controls}</p>
     * @param id 作业票 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/site-controls")
    public ResponseVO<RoadBreakSiteControlVO> addSiteControl(@PathVariable Long id,
                                                               @Valid @RequestBody RoadBreakSiteControlRequest request,
                                                               @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                                               @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                                               @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(roadBreakService.addSiteControl(request.getTenantId(), id, request,
                operator(userId, username, operator)));
    }

    /**
     * 新增前置校验或触发前置校验相关动作。
     * <p>HTTP POST {@code /api/work-permits/{id}/road-break/pre-check}</p>
     * <p>所有查询与变更均按租户隔离。根据 checkPoint 返回是否允许进入下一流程节点。</p>
     * @param id 作业票 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @param checkPoint 流程校验节点编码（如 SUBMIT、SITE_PERMIT）
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/pre-check")
    public ResponseVO<RoadBreakPreCheckResultVO> preCheck(@PathVariable Long id,
                                                           @RequestParam Long tenantId,
                                                           @RequestParam String checkPoint) {
        return ResponseVO.success(roadBreakService.preCheck(tenantId, id, checkPoint));
    }

    /**
     * 查询流程进度。
     * <p>HTTP GET {@code /api/work-permits/{id}/road-break/flow-progress}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 作业票 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/flow-progress")
    public ResponseVO<RoadBreakFlowProgressVO> flowProgress(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(roadBreakService.getFlowProgress(tenantId, id));
    }

    private String operator(String userId, String username, String fallback) {
        return UserContextResolver.operator(userId, username, fallback);
    }
}
