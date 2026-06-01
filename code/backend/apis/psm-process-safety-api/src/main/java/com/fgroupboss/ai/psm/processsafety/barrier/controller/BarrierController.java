package com.fgroupboss.ai.psm.processsafety.barrier.controller;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.processsafety.barrier.model.dto.BarrierDegradeRequest;
import com.fgroupboss.ai.psm.processsafety.barrier.model.dto.BarrierRequest;
import com.fgroupboss.ai.psm.processsafety.barrier.model.dto.BarrierRestoreRequest;
import com.fgroupboss.ai.psm.processsafety.barrier.model.dto.BarrierRuleCheckRequest;
import com.fgroupboss.ai.psm.processsafety.barrier.model.vo.BarrierHealthVO;
import com.fgroupboss.ai.psm.processsafety.barrier.model.vo.BarrierRuleCheckVO;
import com.fgroupboss.ai.psm.processsafety.barrier.model.vo.BarrierVO;
import com.fgroupboss.ai.psm.processsafety.barrier.service.BarrierService;
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
 * Barrier 模块 HTTP API。
 * <p>基础路径：{@code /api/barriers}</p>
 * <p>返回体均为 {@link com.fgroupboss.ai.psm.common.ResponseVO}；写操作需透传租户与操作人上下文。</p>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/barriers")
public class BarrierController {

    private final BarrierService barrierService;

    /**
     * 分页查询列表。
     * <p>HTTP GET {@code /api/barriers}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param tenantId 租户 ID，多租户隔离必填
     * @param keyword 模糊搜索关键字
     * @param status 业务状态筛选
     * @param pageNo 页码，从 1 开始
     * @param pageSize 每页条数，默认 20
     * @return 分页数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping
    public ResponseVO<PageResult<BarrierVO>> page(@RequestParam Long tenantId,
                                                @RequestParam(required = false) String keyword,
                                                @RequestParam(required = false) String status,
                                                @RequestParam(defaultValue = "1") int pageNo,
                                                @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseVO.success(barrierService.page(tenantId, keyword, status, pageNo, pageSize));
    }

    /**
     * 查询单条详情。
     * <p>HTTP GET {@code /api/barriers/{id}}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 资源主键 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/{id}")
    public ResponseVO<BarrierVO> get(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(barrierService.getById(tenantId, id));
    }

    /**
     * 新建记录。
     * <p>HTTP POST {@code /api/barriers}</p>
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping
    public ResponseVO<BarrierVO> create(@Valid @RequestBody BarrierRequest request,
                                        @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(barrierService.create(request, operator));
    }

    /**
     * 更新记录。
     * <p>HTTP PUT {@code /api/barriers/{id}}</p>
     * @param id 资源主键 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PutMapping("/{id}")
    public ResponseVO<BarrierVO> update(@PathVariable Long id,
                                        @Valid @RequestBody BarrierRequest request,
                                        @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(barrierService.update(id, request, operator));
    }

    /**
     * 删除记录。
     * <p>HTTP DELETE {@code /api/barriers/{id}}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 资源主键 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 无业务载荷（成功即可），统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @DeleteMapping("/{id}")
    public ResponseVO<Void> delete(@PathVariable Long id,
                                   @RequestParam Long tenantId,
                                   @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        barrierService.delete(tenantId, id, operator);
        return ResponseVO.success();
    }

    /**
     * 服务健康检查。
     * <p>HTTP GET {@code /api/barriers/{id}/health}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 资源主键 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/{id}/health")
    public ResponseVO<BarrierHealthVO> health(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(barrierService.getHealth(tenantId, id));
    }

    /**
     * 新增degrade或触发degrade相关动作。
     * <p>HTTP POST {@code /api/barriers/{id}/degrade}</p>
     * @param id 资源主键 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/{id}/degrade")
    public ResponseVO<BarrierVO> degrade(@PathVariable Long id,
                                         @Valid @RequestBody BarrierDegradeRequest request,
                                         @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(barrierService.degrade(id, request, operator));
    }

    /**
     * 新增restore或触发restore相关动作。
     * <p>HTTP POST {@code /api/barriers/{id}/restore}</p>
     * @param id 资源主键 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/{id}/restore")
    public ResponseVO<BarrierVO> restore(@PathVariable Long id,
                                         @Valid @RequestBody BarrierRestoreRequest request,
                                         @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(barrierService.restore(id, request, operator));
    }

    /**
     * 新增rule check或触发rule check相关动作。
     * <p>HTTP POST {@code /api/barriers/rule-check}</p>
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/rule-check")
    public ResponseVO<BarrierRuleCheckVO> ruleCheck(@Valid @RequestBody BarrierRuleCheckRequest request) {
        return ResponseVO.success(barrierService.ruleCheck(request));
    }
}
