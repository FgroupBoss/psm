package com.fgroupboss.ai.psm.processsafety.pha.controller;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.common.UserContextHeaders;
import com.fgroupboss.ai.psm.processsafety.pha.model.dto.PhaProjectRequest;
import com.fgroupboss.ai.psm.processsafety.pha.model.vo.PhaProjectReportVO;
import com.fgroupboss.ai.psm.processsafety.pha.model.vo.PhaProjectVO;
import com.fgroupboss.ai.psm.processsafety.pha.service.PhaProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
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

/**
 * PhaProject 模块 HTTP API。
 * <p>工艺危害分析（PHA/HAZOP/LOPA）项目与节点数据。</p>
 * <p>基础路径：{@code /api/pha/projects}</p>
 * <p>返回体均为 {@link com.fgroupboss.ai.psm.common.ResponseVO}；写操作需透传租户与操作人上下文。</p>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pha/projects")
public class PhaProjectController {

    private final PhaProjectService phaProjectService;

    /**
     * 分页查询列表。
     * <p>HTTP GET {@code /api/pha/projects}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param tenantId 租户 ID，多租户隔离必填
     * @param keyword 模糊搜索关键字
     * @param status 业务状态筛选
     * @param pageNo 页码，从 1 开始
     * @param pageSize 每页条数，默认 20
     * @return 分页数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping
    public ResponseVO<PageResult<PhaProjectVO>> page(@RequestParam Long tenantId,
                                                     @RequestParam(required = false) String keyword,
                                                     @RequestParam(required = false) String status,
                                                     @RequestParam(defaultValue = "1") int pageNo,
                                                     @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseVO.success(phaProjectService.page(tenantId, keyword, status, pageNo, pageSize));
    }

    /**
     * 查询单条详情。
     * <p>HTTP GET {@code /api/pha/projects/{id}}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 资源主键 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/{id}")
    public ResponseVO<PhaProjectVO> get(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(phaProjectService.getById(tenantId, id));
    }

    /**
     * 新建记录。
     * <p>HTTP POST {@code /api/pha/projects}</p>
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping
    public ResponseVO<PhaProjectVO> create(@Valid @RequestBody PhaProjectRequest request,
                                           @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                           @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                           @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(phaProjectService.create(request, resolveOperator(userId, username, operator)));
    }

    /**
     * 更新记录。
     * <p>HTTP PUT {@code /api/pha/projects/{id}}</p>
     * @param id 资源主键 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PutMapping("/{id}")
    public ResponseVO<PhaProjectVO> update(@PathVariable Long id,
                                           @Valid @RequestBody PhaProjectRequest request,
                                           @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                           @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                           @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(phaProjectService.update(id, request, resolveOperator(userId, username, operator)));
    }

    /**
     * 删除记录。
     * <p>HTTP DELETE {@code /api/pha/projects/{id}}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 资源主键 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 无业务载荷（成功即可），统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @DeleteMapping("/{id}")
    public ResponseVO<Void> delete(@PathVariable Long id,
                                   @RequestParam Long tenantId,
                                   @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        phaProjectService.delete(tenantId, id, operator);
        return ResponseVO.success();
    }

    /**
     * 提交审批。
     * <p>HTTP POST {@code /api/pha/projects/{id}/submit}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 资源主键 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/{id}/submit")
    public ResponseVO<PhaProjectVO> submit(@PathVariable Long id, @RequestParam Long tenantId,
                                           @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(phaProjectService.submit(tenantId, id, operator));
    }

    /**
     * 新增publish或触发publish相关动作。
     * <p>HTTP POST {@code /api/pha/projects/{id}/publish}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 资源主键 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/{id}/publish")
    public ResponseVO<PhaProjectVO> publish(@PathVariable Long id, @RequestParam Long tenantId,
                                            @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(phaProjectService.publish(tenantId, id, operator));
    }

    /**
     * 新增archive或触发archive相关动作。
     * <p>HTTP POST {@code /api/pha/projects/{id}/archive}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 资源主键 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/{id}/archive")
    public ResponseVO<PhaProjectVO> archive(@PathVariable Long id, @RequestParam Long tenantId,
                                            @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(phaProjectService.archive(tenantId, id, operator));
    }

    /**
     * 查询report。
     * <p>HTTP GET {@code /api/pha/projects/{id}/report}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 资源主键 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/{id}/report")
    public ResponseVO<PhaProjectReportVO> report(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(phaProjectService.exportReport(id, tenantId));
    }

    private String resolveOperator(String userId, String username, String operator) {
        if (username != null && !username.isEmpty()) {
            return username;
        }
        if (userId != null && !userId.isEmpty()) {
            return userId;
        }
        return operator;
    }
}
