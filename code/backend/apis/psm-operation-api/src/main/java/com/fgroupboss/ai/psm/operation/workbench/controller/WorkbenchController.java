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
 * <p>基础路径：{@code /api/workbench}</p>
 * <p>返回体均为 {@link com.fgroupboss.ai.psm.common.ResponseVO}；用户 ID 可由查询参数或 {@code X-User-Id} 请求头提供。</p>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/workbench")
public class WorkbenchController {

    private final WorkbenchTodoService workbenchTodoService;

    /**
     * 查询工作台待办列表（复用移动端任务模型）。
     * <p>HTTP GET {@code /api/workbench/todos}</p>
     *
     * @param tenantId      租户 ID
     * @param role          角色视角（可选）
     * @param taskType      任务类型过滤（可选）
     * @param userId        用户 ID（查询参数，可选）
     * @param userIdHeader  用户 ID（请求头 {@link UserContextHeaders#USER_ID}，与 userId 二选一）
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/todos")
    public ResponseVO<List<MobileTaskVO>> todos(@RequestParam Long tenantId,
                                                @RequestParam(required = false) String role,
                                                @RequestParam(required = false) String taskType,
                                                @RequestParam(required = false) Long userId,
                                                @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userIdHeader) {
        Long resolvedUserId = resolveUserId(userId, userIdHeader);
        return ResponseVO.success(workbenchTodoService.listTodos(tenantId, resolvedUserId, role, taskType));
    }

    /**
     * 按任务类型聚合待办数量（角标/分组统计）。
     * <p>HTTP GET {@code /api/workbench/todos/count}</p>
     *
     * @param tenantId      租户 ID
     * @param role          角色视角（可选）
     * @param userId        用户 ID（查询参数，可选）
     * @param userIdHeader  用户 ID（请求头，与 userId 二选一）
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
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
