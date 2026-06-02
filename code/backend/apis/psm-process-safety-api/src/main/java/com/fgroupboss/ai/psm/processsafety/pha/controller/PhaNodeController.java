package com.fgroupboss.ai.psm.processsafety.pha.controller;

import com.fgroupboss.ai.psm.common.LoginContext;
import com.fgroupboss.ai.psm.common.UserContext;
import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.processsafety.pha.model.dto.PhaNodeRequest;
import com.fgroupboss.ai.psm.processsafety.pha.model.vo.PhaNodeVO;
import com.fgroupboss.ai.psm.processsafety.pha.service.PhaNodeService;
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
 * PhaNode 模块 HTTP API。
 * <p>工艺危害分析（PHA/HAZOP/LOPA）项目与节点数据。</p>
 * <p>基础路径：{@code /api/pha/projects/{projectId}/nodes}</p>
 * <p>返回体均为 {@link com.fgroupboss.ai.psm.common.ResponseVO}；写操作需透传租户与操作人上下文。</p>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pha/projects/{projectId}/nodes")
public class PhaNodeController {

    private final PhaNodeService phaNodeService;

    /**
     * 查询列表。
     * <p>HTTP GET {@code /api/pha/projects/{projectId}/nodes}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param projectId project ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 列表数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping
    public ResponseVO<List<PhaNodeVO>> list(@LoginContext UserContext loginContext,
                                                  @PathVariable Long projectId) {
        return ResponseVO.success(phaNodeService.listByProject(loginContext.getTenantId(), projectId));
    }

    /**
     * 新建记录。
     * <p>HTTP POST {@code /api/pha/projects/{projectId}/nodes}</p>
     * @param projectId project ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping
    public ResponseVO<PhaNodeVO> create(@PathVariable Long projectId,
                                        @Valid @RequestBody PhaNodeRequest request,
                                        @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(phaNodeService.create(projectId, request, operator));
    }
}
