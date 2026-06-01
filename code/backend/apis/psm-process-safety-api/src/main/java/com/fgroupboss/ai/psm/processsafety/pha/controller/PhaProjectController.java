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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pha/projects")
public class PhaProjectController {

    private final PhaProjectService phaProjectService;

    @GetMapping
    public ResponseVO<PageResult<PhaProjectVO>> page(@RequestParam Long tenantId,
                                                     @RequestParam(required = false) String keyword,
                                                     @RequestParam(required = false) String status,
                                                     @RequestParam(defaultValue = "1") int pageNo,
                                                     @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseVO.success(phaProjectService.page(tenantId, keyword, status, pageNo, pageSize));
    }

    @GetMapping("/{id}")
    public ResponseVO<PhaProjectVO> get(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(phaProjectService.getById(tenantId, id));
    }

    @PostMapping
    public ResponseVO<PhaProjectVO> create(@Valid @RequestBody PhaProjectRequest request,
                                           @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                           @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                           @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(phaProjectService.create(request, resolveOperator(userId, username, operator)));
    }

    @PutMapping("/{id}")
    public ResponseVO<PhaProjectVO> update(@PathVariable Long id,
                                           @Valid @RequestBody PhaProjectRequest request,
                                           @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                           @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                           @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(phaProjectService.update(id, request, resolveOperator(userId, username, operator)));
    }

    @DeleteMapping("/{id}")
    public ResponseVO<Void> delete(@PathVariable Long id,
                                   @RequestParam Long tenantId,
                                   @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        phaProjectService.delete(tenantId, id, operator);
        return ResponseVO.success();
    }

    @PostMapping("/{id}/submit")
    public ResponseVO<PhaProjectVO> submit(@PathVariable Long id, @RequestParam Long tenantId,
                                           @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(phaProjectService.submit(tenantId, id, operator));
    }

    @PostMapping("/{id}/publish")
    public ResponseVO<PhaProjectVO> publish(@PathVariable Long id, @RequestParam Long tenantId,
                                            @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(phaProjectService.publish(tenantId, id, operator));
    }

    @PostMapping("/{id}/archive")
    public ResponseVO<PhaProjectVO> archive(@PathVariable Long id, @RequestParam Long tenantId,
                                            @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(phaProjectService.archive(tenantId, id, operator));
    }

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
