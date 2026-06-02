package com.fgroupboss.ai.psm.processsafety.pha.controller;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.common.LoginContext;
import com.fgroupboss.ai.psm.common.UserContext;
import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.processsafety.pha.model.dto.PhaRecommendationRequest;
import com.fgroupboss.ai.psm.processsafety.pha.model.dto.RecommendationActionRequest;
import com.fgroupboss.ai.psm.processsafety.pha.model.vo.PhaRecommendationVO;
import com.fgroupboss.ai.psm.processsafety.pha.service.PhaRecommendationService;
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

/**
 * PhaRecommendation 模块 HTTP API。
 * <p>工艺危害分析（PHA/HAZOP/LOPA）项目与节点数据。</p>
 * <p>基础路径：{@code /api/pha/recommendations}</p>
 * <p>返回体均为 {@link com.fgroupboss.ai.psm.common.ResponseVO}；写操作需透传租户与操作人上下文。</p>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pha/recommendations")
public class PhaRecommendationController {

    private final PhaRecommendationService phaRecommendationService;

    /**
     * 分页查询列表。
     * <p>HTTP GET {@code /api/pha/recommendations}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param tenantId 租户 ID，多租户隔离必填
     * @param projectId project ID
     * @param status 业务状态筛选
     * @param pageNo 页码，从 1 开始
     * @param pageSize 每页条数，默认 20
     * @return 分页数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping
    public ResponseVO<PageResult<PhaRecommendationVO>> page(@LoginContext UserContext loginContext,
                                                                                                              @RequestParam(required = false) Long projectId,
                                                            @RequestParam(required = false) String status,
                                                            @RequestParam(defaultValue = "1") int pageNo,
                                                            @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseVO.success(phaRecommendationService.page(loginContext.getTenantId(), projectId, status, pageNo, pageSize));
    }

    /**
     * 查询列表。
     * <p>HTTP GET {@code /api/pha/recommendations/list}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param tenantId 租户 ID，多租户隔离必填
     * @param projectId project ID
     * @param status 业务状态筛选
     * @return 列表数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/list")
    public ResponseVO<List<PhaRecommendationVO>> list(@LoginContext UserContext loginContext,
                                        @RequestParam(required = false) Long projectId,
                                                      @RequestParam(required = false) String status) {
        return ResponseVO.success(phaRecommendationService.list(loginContext.getTenantId(), projectId, status));
    }

    /**
     * 新建记录。
     * <p>HTTP POST {@code /api/pha/recommendations}</p>
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping
    public ResponseVO<PhaRecommendationVO> create(@Valid @RequestBody PhaRecommendationRequest request,
                                                  @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(phaRecommendationService.create(request, operator));
    }

    /**
     * 新增assign或触发assign相关动作。
     * <p>HTTP POST {@code /api/pha/recommendations/{id}/assign}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 资源主键 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/{id}/assign")
    public ResponseVO<PhaRecommendationVO> assign(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                                                                                      @RequestBody RecommendationActionRequest request,
                                                   @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(phaRecommendationService.assign(loginContext.getTenantId(), id, request, operator));
    }

    /**
     * 新增rectify或触发rectify相关动作。
     * <p>HTTP POST {@code /api/pha/recommendations/{id}/rectify}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 资源主键 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/{id}/rectify")
    public ResponseVO<PhaRecommendationVO> rectify(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                                                                                      @RequestBody RecommendationActionRequest request,
                                                   @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(phaRecommendationService.rectify(loginContext.getTenantId(), id, request, operator));
    }

    /**
     * 新增verify或触发verify相关动作。
     * <p>HTTP POST {@code /api/pha/recommendations/{id}/verify}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 资源主键 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/{id}/verify")
    public ResponseVO<PhaRecommendationVO> verify(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                                                                                      @RequestBody RecommendationActionRequest request,
                                                   @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(phaRecommendationService.verify(loginContext.getTenantId(), id, request, operator));
    }

    /**
     * 新增close或触发close相关动作。
     * <p>HTTP POST {@code /api/pha/recommendations/{id}/close}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 资源主键 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/{id}/close")
    public ResponseVO<PhaRecommendationVO> close(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                                                                                  @RequestBody RecommendationActionRequest request,
                                                 @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(phaRecommendationService.close(loginContext.getTenantId(), id, request, operator));
    }
}
