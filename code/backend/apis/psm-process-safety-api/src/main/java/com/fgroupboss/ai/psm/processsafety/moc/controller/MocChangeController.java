package com.fgroupboss.ai.psm.processsafety.moc.controller;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.common.UserContextHeaders;
import com.fgroupboss.ai.psm.common.UserContextResolver;
import com.fgroupboss.ai.psm.processsafety.moc.model.dto.MocApproveRequest;
import com.fgroupboss.ai.psm.processsafety.moc.model.dto.MocChangeRequest;
import com.fgroupboss.ai.psm.processsafety.moc.model.dto.MocImpactAnalysisRequest;
import com.fgroupboss.ai.psm.processsafety.moc.model.dto.MocImplementationTaskRequest;
import com.fgroupboss.ai.psm.processsafety.moc.model.dto.MocVerifyRequest;
import com.fgroupboss.ai.psm.processsafety.moc.model.vo.MocChangeVO;
import com.fgroupboss.ai.psm.processsafety.moc.model.vo.MocImpactAnalysisVO;
import com.fgroupboss.ai.psm.processsafety.moc.model.vo.MocImplementationTaskVO;
import com.fgroupboss.ai.psm.processsafety.moc.service.MocChangeService;
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
import java.util.List;

/**
 * MocChange 模块 HTTP API。
 * <p>基础路径：{@code /api/moc/changes}</p>
 * <p>返回体均为 {@link com.fgroupboss.ai.psm.common.ResponseVO}；写操作需透传租户与操作人上下文。</p>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/moc/changes")
public class MocChangeController {

    private final MocChangeService mocChangeService;

    /**
     * 分页查询列表。
     * <p>HTTP GET {@code /api/moc/changes}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param tenantId 租户 ID，多租户隔离必填
     * @param keyword 模糊搜索关键字
     * @param status 业务状态筛选
     * @param pageNo 页码，从 1 开始
     * @param pageSize 每页条数，默认 20
     * @return 分页数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping
    public ResponseVO<PageResult<MocChangeVO>> page(@RequestParam Long tenantId,
                                                    @RequestParam(required = false) String keyword,
                                                    @RequestParam(required = false) String status,
                                                    @RequestParam(defaultValue = "1") int pageNo,
                                                    @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseVO.success(mocChangeService.page(tenantId, keyword, status, pageNo, pageSize));
    }

    /**
     * 查询单条详情。
     * <p>HTTP GET {@code /api/moc/changes/{id}}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 资源主键 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/{id}")
    public ResponseVO<MocChangeVO> get(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(mocChangeService.getById(tenantId, id));
    }

    /**
     * 新建记录。
     * <p>HTTP POST {@code /api/moc/changes}</p>
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping
    public ResponseVO<MocChangeVO> create(@Valid @RequestBody MocChangeRequest request,
                                          @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                          @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                          @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(mocChangeService.create(request, resolveOperator(userId, username, operator)));
    }

    /**
     * 更新记录。
     * <p>HTTP PUT {@code /api/moc/changes/{id}}</p>
     * @param id 资源主键 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PutMapping("/{id}")
    public ResponseVO<MocChangeVO> update(@PathVariable Long id,
                                          @Valid @RequestBody MocChangeRequest request,
                                          @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                          @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                          @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(mocChangeService.update(id, request, resolveOperator(userId, username, operator)));
    }

    /**
     * 删除记录。
     * <p>HTTP DELETE {@code /api/moc/changes/{id}}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 资源主键 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 无业务载荷（成功即可），统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @DeleteMapping("/{id}")
    public ResponseVO<Void> delete(@PathVariable Long id,
                                   @RequestParam Long tenantId,
                                   @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        mocChangeService.delete(tenantId, id, operator);
        return ResponseVO.success();
    }

    /**
     * 提交审批。
     * <p>HTTP POST {@code /api/moc/changes/{id}/submit}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 资源主键 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/{id}/submit")
    public ResponseVO<MocChangeVO> submit(@PathVariable Long id,
                                          @RequestParam Long tenantId,
                                          @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(mocChangeService.submit(tenantId, id, operator));
    }

    /**
     * 查询impact analysis。
     * <p>HTTP GET {@code /api/moc/changes/{id}/impact-analysis}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 资源主键 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 列表数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/{id}/impact-analysis")
    public ResponseVO<List<MocImpactAnalysisVO>> listImpactAnalysis(@PathVariable Long id,
                                                                    @RequestParam Long tenantId) {
        return ResponseVO.success(mocChangeService.listImpactAnalysis(tenantId, id));
    }

    /**
     * 新增impact analysis或触发impact analysis相关动作。
     * <p>HTTP POST {@code /api/moc/changes/{id}/impact-analysis}</p>
     * @param id 资源主键 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/{id}/impact-analysis")
    public ResponseVO<MocImpactAnalysisVO> saveImpactAnalysis(@PathVariable Long id,
                                                              @Valid @RequestBody MocImpactAnalysisRequest request,
                                                              @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                                              @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                                              @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(mocChangeService.saveImpactAnalysis(id, request,
                resolveOperator(userId, username, operator)));
    }

    /**
     * 审批通过。
     * <p>HTTP POST {@code /api/moc/changes/{id}/approve}</p>
     * @param id 资源主键 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/{id}/approve")
    public ResponseVO<MocChangeVO> approve(@PathVariable Long id,
                                           @Valid @RequestBody MocApproveRequest request,
                                           @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                           @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                           @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(mocChangeService.approve(id, request,
                resolveOperator(userId, username, operator)));
    }

    /**
     * 查询implementation tasks。
     * <p>HTTP GET {@code /api/moc/changes/{id}/implementation-tasks}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 资源主键 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 列表数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/{id}/implementation-tasks")
    public ResponseVO<List<MocImplementationTaskVO>> listImplementationTasks(@PathVariable Long id,
                                                                             @RequestParam Long tenantId) {
        return ResponseVO.success(mocChangeService.listImplementationTasks(tenantId, id));
    }

    /**
     * 新增implementation tasks或触发implementation tasks相关动作。
     * <p>HTTP POST {@code /api/moc/changes/{id}/implementation-tasks}</p>
     * @param id 资源主键 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/{id}/implementation-tasks")
    public ResponseVO<MocImplementationTaskVO> createImplementationTask(@PathVariable Long id,
                                                                      @Valid @RequestBody MocImplementationTaskRequest request,
                                                                      @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                                                      @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                                                      @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(mocChangeService.createImplementationTask(id, request,
                resolveOperator(userId, username, operator)));
    }

    /**
     * 新增verify或触发verify相关动作。
     * <p>HTTP POST {@code /api/moc/changes/{id}/verify}</p>
     * @param id 资源主键 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/{id}/verify")
    public ResponseVO<MocChangeVO> verify(@PathVariable Long id,
                                          @Valid @RequestBody MocVerifyRequest request,
                                          @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                          @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                          @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(mocChangeService.verify(id, request,
                resolveOperator(userId, username, operator)));
    }

    /**
     * 新增close或触发close相关动作。
     * <p>HTTP POST {@code /api/moc/changes/{id}/close}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 资源主键 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/{id}/close")
    public ResponseVO<MocChangeVO> close(@PathVariable Long id,
                                         @RequestParam Long tenantId,
                                         @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(mocChangeService.close(tenantId, id, operator));
    }

    private String resolveOperator(String userId, String username, String fallback) {
        return UserContextResolver.operator(userId, username, fallback);
    }
}
