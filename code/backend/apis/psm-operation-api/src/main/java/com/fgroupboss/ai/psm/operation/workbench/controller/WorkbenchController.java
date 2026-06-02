package com.fgroupboss.ai.psm.operation.workbench.controller;

import com.fgroupboss.ai.psm.common.LoginContext;
import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.common.UserContext;
import com.fgroupboss.ai.psm.operation.mobile.model.vo.MobileTaskVO;
import com.fgroupboss.ai.psm.operation.workbench.model.vo.WorkbenchTodoCountVO;
import com.fgroupboss.ai.psm.operation.workbench.service.WorkbenchTodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Web 工作台待办 API。
 * <p>基础路径：{@code /api/workbench}</p>
 * <p>返回体均为 {@link com.fgroupboss.ai.psm.common.ResponseVO}；租户与用户 ID 从登录上下文解析。</p>
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
     * @param loginContext 当前登录租户与用户
     * @param role         角色视角（可选）
     * @param taskType     任务类型过滤（可选）
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/todos")
    public ResponseVO<List<MobileTaskVO>> todos(@LoginContext UserContext loginContext,
                                                @RequestParam(required = false) String role,
                                                @RequestParam(required = false) String taskType) {
        return ResponseVO.success(workbenchTodoService.listTodos(
                loginContext.getTenantId(), loginContext.getUserId(), role, taskType));
    }

    /**
     * 按任务类型聚合待办数量（角标/分组统计）。
     * <p>HTTP GET {@code /api/workbench/todos/count}</p>
     *
     * @param loginContext 当前登录租户与用户
     * @param role         角色视角（可选）
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/todos/count")
    public ResponseVO<WorkbenchTodoCountVO> todoCount(@LoginContext UserContext loginContext,
                                                      @RequestParam(required = false) String role) {
        return ResponseVO.success(workbenchTodoService.countTodos(
                loginContext.getTenantId(), loginContext.getUserId(), role));
    }
}
