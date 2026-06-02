package com.fgroupboss.ai.psm.operation.workbench.controller;

import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.common.UserContextHeaders;
import com.fgroupboss.ai.psm.operation.mobile.model.vo.MobileTaskVO;
import com.fgroupboss.ai.psm.operation.workbench.model.vo.WorkbenchTodoCountVO;
import com.fgroupboss.ai.psm.operation.workbench.service.WorkbenchTodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Web 工作台待办 API。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/workbench")
public class WorkbenchController {

    private final WorkbenchTodoService workbenchTodoService;

    @GetMapping("/todos")
    public ResponseVO<List<MobileTaskVO>> todos(@RequestParam Long tenantId,
                                                @RequestParam(required = false) String role,
                                                @RequestParam(required = false) String taskType,
                                                @RequestParam(required = false) Long userId,
                                                @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userIdHeader) {
        Long resolvedUserId = resolveUserId(userId, userIdHeader);
        return ResponseVO.success(workbenchTodoService.listTodos(tenantId, resolvedUserId, role, taskType));
    }

    @GetMapping("/todos/count")
    public ResponseVO<WorkbenchTodoCountVO> todoCount(@RequestParam Long tenantId,
                                                      @RequestParam(required = false) String role,
                                                      @RequestParam(required = false) Long userId,
                                                      @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userIdHeader) {
        Long resolvedUserId = resolveUserId(userId, userIdHeader);
        return ResponseVO.success(workbenchTodoService.countTodos(tenantId, resolvedUserId, role));
    }

    private Long resolveUserId(Long userId, String userIdHeader) {
        if (userId != null) {
            return userId;
        }
        if (!StringUtils.hasText(userIdHeader)) {
            throw new BusinessException(400, "userId is required");
        }
        try {
            return Long.valueOf(userIdHeader.trim());
        } catch (NumberFormatException ex) {
            throw new BusinessException(400, "invalid user id header");
        }
    }
}
