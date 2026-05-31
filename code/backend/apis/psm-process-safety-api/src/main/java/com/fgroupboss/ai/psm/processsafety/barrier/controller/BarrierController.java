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
 * 安全屏障接口。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/barriers")
public class BarrierController {

    private final BarrierService barrierService;

    @GetMapping
    public ResponseVO<PageResult<BarrierVO>> page(@RequestParam Long tenantId,
                                                @RequestParam(required = false) String keyword,
                                                @RequestParam(required = false) String status,
                                                @RequestParam(defaultValue = "1") int pageNo,
                                                @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseVO.success(barrierService.page(tenantId, keyword, status, pageNo, pageSize));
    }

    @GetMapping("/{id}")
    public ResponseVO<BarrierVO> get(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(barrierService.getById(tenantId, id));
    }

    @PostMapping
    public ResponseVO<BarrierVO> create(@Valid @RequestBody BarrierRequest request,
                                        @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(barrierService.create(request, operator));
    }

    @PutMapping("/{id}")
    public ResponseVO<BarrierVO> update(@PathVariable Long id,
                                        @Valid @RequestBody BarrierRequest request,
                                        @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(barrierService.update(id, request, operator));
    }

    @DeleteMapping("/{id}")
    public ResponseVO<Void> delete(@PathVariable Long id,
                                   @RequestParam Long tenantId,
                                   @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        barrierService.delete(tenantId, id, operator);
        return ResponseVO.success();
    }

    @GetMapping("/{id}/health")
    public ResponseVO<BarrierHealthVO> health(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(barrierService.getHealth(tenantId, id));
    }

    @PostMapping("/{id}/degrade")
    public ResponseVO<BarrierVO> degrade(@PathVariable Long id,
                                         @Valid @RequestBody BarrierDegradeRequest request,
                                         @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(barrierService.degrade(id, request, operator));
    }

    @PostMapping("/{id}/restore")
    public ResponseVO<BarrierVO> restore(@PathVariable Long id,
                                         @Valid @RequestBody BarrierRestoreRequest request,
                                         @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(barrierService.restore(id, request, operator));
    }

    @PostMapping("/rule-check")
    public ResponseVO<BarrierRuleCheckVO> ruleCheck(@Valid @RequestBody BarrierRuleCheckRequest request) {
        return ResponseVO.success(barrierService.ruleCheck(request));
    }
}
