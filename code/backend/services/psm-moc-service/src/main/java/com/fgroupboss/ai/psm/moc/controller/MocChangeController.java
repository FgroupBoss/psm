package com.fgroupboss.ai.psm.moc.controller;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.common.UserContextHeaders;
import com.fgroupboss.ai.psm.common.UserContextResolver;
import com.fgroupboss.ai.psm.moc.model.dto.MocApproveRequest;
import com.fgroupboss.ai.psm.moc.model.dto.MocChangeRequest;
import com.fgroupboss.ai.psm.moc.model.dto.MocImpactAnalysisRequest;
import com.fgroupboss.ai.psm.moc.model.dto.MocImplementationTaskRequest;
import com.fgroupboss.ai.psm.moc.model.dto.MocVerifyRequest;
import com.fgroupboss.ai.psm.moc.model.vo.MocChangeVO;
import com.fgroupboss.ai.psm.moc.model.vo.MocImpactAnalysisVO;
import com.fgroupboss.ai.psm.moc.model.vo.MocImplementationTaskVO;
import com.fgroupboss.ai.psm.moc.service.MocChangeService;
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
 * MOC 变更管理接口。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/moc/changes")
public class MocChangeController {

    private final MocChangeService mocChangeService;

    @GetMapping
    public ResponseVO<PageResult<MocChangeVO>> page(@RequestParam Long tenantId,
                                                    @RequestParam(required = false) String keyword,
                                                    @RequestParam(required = false) String status,
                                                    @RequestParam(defaultValue = "1") int pageNo,
                                                    @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseVO.success(mocChangeService.page(tenantId, keyword, status, pageNo, pageSize));
    }

    @GetMapping("/{id}")
    public ResponseVO<MocChangeVO> get(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(mocChangeService.getById(tenantId, id));
    }

    @PostMapping
    public ResponseVO<MocChangeVO> create(@Valid @RequestBody MocChangeRequest request,
                                          @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                          @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                          @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(mocChangeService.create(request, resolveOperator(userId, username, operator)));
    }

    @PutMapping("/{id}")
    public ResponseVO<MocChangeVO> update(@PathVariable Long id,
                                          @Valid @RequestBody MocChangeRequest request,
                                          @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                          @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                          @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(mocChangeService.update(id, request, resolveOperator(userId, username, operator)));
    }

    @DeleteMapping("/{id}")
    public ResponseVO<Void> delete(@PathVariable Long id,
                                   @RequestParam Long tenantId,
                                   @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        mocChangeService.delete(tenantId, id, operator);
        return ResponseVO.success();
    }

    @PostMapping("/{id}/submit")
    public ResponseVO<MocChangeVO> submit(@PathVariable Long id,
                                          @RequestParam Long tenantId,
                                          @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(mocChangeService.submit(tenantId, id, operator));
    }

    @GetMapping("/{id}/impact-analysis")
    public ResponseVO<List<MocImpactAnalysisVO>> listImpactAnalysis(@PathVariable Long id,
                                                                    @RequestParam Long tenantId) {
        return ResponseVO.success(mocChangeService.listImpactAnalysis(tenantId, id));
    }

    @PostMapping("/{id}/impact-analysis")
    public ResponseVO<MocImpactAnalysisVO> saveImpactAnalysis(@PathVariable Long id,
                                                              @Valid @RequestBody MocImpactAnalysisRequest request,
                                                              @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                                              @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                                              @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(mocChangeService.saveImpactAnalysis(id, request,
                resolveOperator(userId, username, operator)));
    }

    @PostMapping("/{id}/approve")
    public ResponseVO<MocChangeVO> approve(@PathVariable Long id,
                                           @Valid @RequestBody MocApproveRequest request,
                                           @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                           @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                           @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(mocChangeService.approve(id, request,
                resolveOperator(userId, username, operator)));
    }

    @GetMapping("/{id}/implementation-tasks")
    public ResponseVO<List<MocImplementationTaskVO>> listImplementationTasks(@PathVariable Long id,
                                                                             @RequestParam Long tenantId) {
        return ResponseVO.success(mocChangeService.listImplementationTasks(tenantId, id));
    }

    @PostMapping("/{id}/implementation-tasks")
    public ResponseVO<MocImplementationTaskVO> createImplementationTask(@PathVariable Long id,
                                                                      @Valid @RequestBody MocImplementationTaskRequest request,
                                                                      @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                                                      @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                                                      @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(mocChangeService.createImplementationTask(id, request,
                resolveOperator(userId, username, operator)));
    }

    @PostMapping("/{id}/verify")
    public ResponseVO<MocChangeVO> verify(@PathVariable Long id,
                                          @Valid @RequestBody MocVerifyRequest request,
                                          @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                          @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                          @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(mocChangeService.verify(id, request,
                resolveOperator(userId, username, operator)));
    }

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
