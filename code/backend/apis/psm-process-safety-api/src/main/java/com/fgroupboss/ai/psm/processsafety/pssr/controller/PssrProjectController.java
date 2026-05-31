package com.fgroupboss.ai.psm.processsafety.pssr.controller;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.common.UserContextHeaders;
import com.fgroupboss.ai.psm.processsafety.pssr.model.dto.PssrApprovalRequest;
import com.fgroupboss.ai.psm.processsafety.pssr.model.dto.PssrExecuteRequest;
import com.fgroupboss.ai.psm.processsafety.pssr.model.dto.PssrProjectRequest;
import com.fgroupboss.ai.psm.processsafety.pssr.model.vo.PssrExecutionRecordVO;
import com.fgroupboss.ai.psm.processsafety.pssr.model.vo.PssrProjectVO;
import com.fgroupboss.ai.psm.processsafety.pssr.model.vo.PssrStartupCheckVO;
import com.fgroupboss.ai.psm.processsafety.pssr.service.PssrProjectService;
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
 * PSSR 项目接口。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pssr/projects")
public class PssrProjectController {

    private final PssrProjectService pssrProjectService;

    @GetMapping
    public ResponseVO<PageResult<PssrProjectVO>> page(@RequestParam Long tenantId,
                                                    @RequestParam(required = false) String keyword,
                                                    @RequestParam(required = false) String status,
                                                    @RequestParam(defaultValue = "1") int pageNo,
                                                    @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseVO.success(pssrProjectService.page(tenantId, keyword, status, pageNo, pageSize));
    }

    @GetMapping("/{id}")
    public ResponseVO<PssrProjectVO> get(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(pssrProjectService.getById(tenantId, id));
    }

    @PostMapping
    public ResponseVO<PssrProjectVO> create(@Valid @RequestBody PssrProjectRequest request,
                                            @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                            @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                            @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(pssrProjectService.create(request, resolveOperator(userId, username, operator)));
    }

    @PutMapping("/{id}")
    public ResponseVO<PssrProjectVO> update(@PathVariable Long id,
                                            @Valid @RequestBody PssrProjectRequest request,
                                            @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                            @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                            @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(pssrProjectService.update(id, request, resolveOperator(userId, username, operator)));
    }

    @DeleteMapping("/{id}")
    public ResponseVO<Void> delete(@PathVariable Long id,
                                   @RequestParam Long tenantId,
                                   @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                   @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                   @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        pssrProjectService.delete(tenantId, id, resolveOperator(userId, username, operator));
        return ResponseVO.success();
    }

    @PostMapping("/{id}/execute")
    public ResponseVO<PssrExecutionRecordVO> execute(@PathVariable Long id,
                                                     @Valid @RequestBody PssrExecuteRequest request,
                                                     @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                                     @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                                     @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(pssrProjectService.execute(id, request, resolveOperator(userId, username, operator)));
    }

    @PostMapping("/{id}/approve-startup")
    public ResponseVO<PssrProjectVO> approveStartup(@PathVariable Long id,
                                                    @Valid @RequestBody PssrApprovalRequest request,
                                                    @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                                    @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                                    @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(pssrProjectService.approveStartup(id, request, resolveOperator(userId, username, operator)));
    }

    @PostMapping("/{id}/reject-startup")
    public ResponseVO<PssrProjectVO> rejectStartup(@PathVariable Long id,
                                                   @Valid @RequestBody PssrApprovalRequest request,
                                                   @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                                   @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                                   @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(pssrProjectService.rejectStartup(id, request, resolveOperator(userId, username, operator)));
    }

    @GetMapping("/{id}/startup-check")
    public ResponseVO<PssrStartupCheckVO> startupCheck(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(pssrProjectService.startupCheck(tenantId, id));
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
