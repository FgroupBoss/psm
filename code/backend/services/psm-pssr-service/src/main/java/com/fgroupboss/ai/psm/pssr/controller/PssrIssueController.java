package com.fgroupboss.ai.psm.pssr.controller;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.common.UserContextHeaders;
import com.fgroupboss.ai.psm.pssr.model.dto.PssrIssueActionRequest;
import com.fgroupboss.ai.psm.pssr.model.dto.PssrIssueRequest;
import com.fgroupboss.ai.psm.pssr.model.vo.PssrIssueVO;
import com.fgroupboss.ai.psm.pssr.service.PssrIssueService;
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

/**
 * PSSR 审查问题接口。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pssr/issues")
public class PssrIssueController {

    private final PssrIssueService pssrIssueService;

    @GetMapping
    public ResponseVO<PageResult<PssrIssueVO>> page(@RequestParam Long tenantId,
                                                  @RequestParam(required = false) Long projectId,
                                                  @RequestParam(required = false) String status,
                                                  @RequestParam(defaultValue = "1") int pageNo,
                                                  @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseVO.success(pssrIssueService.page(tenantId, projectId, status, pageNo, pageSize));
    }

    @PostMapping
    public ResponseVO<PssrIssueVO> create(@Valid @RequestBody PssrIssueRequest request,
                                          @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(pssrIssueService.create(request, operator));
    }

    @PostMapping("/{id}/rectify")
    public ResponseVO<PssrIssueVO> rectify(@PathVariable Long id,
                                           @Valid @RequestBody PssrIssueActionRequest request,
                                           @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                           @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        String op = username != null && !username.isEmpty() ? username : operator;
        return ResponseVO.success(pssrIssueService.rectify(id, request, op));
    }

    @PostMapping("/{id}/review")
    public ResponseVO<PssrIssueVO> review(@PathVariable Long id,
                                          @Valid @RequestBody PssrIssueActionRequest request,
                                          @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                          @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        String op = username != null && !username.isEmpty() ? username : operator;
        return ResponseVO.success(pssrIssueService.review(id, request, op));
    }
}
