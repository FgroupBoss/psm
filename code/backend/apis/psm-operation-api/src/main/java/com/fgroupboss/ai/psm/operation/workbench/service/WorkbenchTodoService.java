package com.fgroupboss.ai.psm.operation.workbench.service;

import com.fgroupboss.ai.psm.operation.mobile.config.MobileRole;
import com.fgroupboss.ai.psm.operation.mobile.model.vo.MobileTaskVO;
import com.fgroupboss.ai.psm.operation.mobile.service.MobileTaskService;
import com.fgroupboss.ai.psm.operation.workbench.model.vo.WorkbenchTodoCountVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Web 工作台待办聚合，复用移动端任务查询逻辑。
 */
@Service
@RequiredArgsConstructor
public class WorkbenchTodoService {

    private final MobileTaskService mobileTaskService;

    public List<MobileTaskVO> listTodos(Long tenantId, Long userId, String role, String taskType) {
        List<MobileTaskVO> tasks = mobileTaskService.listTasks(tenantId, userId, MobileRole.from(role));
        if (!StringUtils.hasText(taskType)) {
            return tasks;
        }
        String normalized = taskType.trim().toUpperCase();
        List<MobileTaskVO> filtered = new ArrayList<MobileTaskVO>();
        for (MobileTaskVO task : tasks) {
            if (task != null && normalized.equals(task.getTaskType())) {
                filtered.add(task);
            }
        }
        return filtered;
    }

    public WorkbenchTodoCountVO countTodos(Long tenantId, Long userId, String role) {
        List<MobileTaskVO> tasks = mobileTaskService.listTasks(tenantId, userId, MobileRole.from(role));
        WorkbenchTodoCountVO vo = new WorkbenchTodoCountVO();
        for (MobileTaskVO task : tasks) {
            if (task == null || !StringUtils.hasText(task.getTaskType())) {
                continue;
            }
            vo.setTotal(vo.getTotal() + 1);
            String type = task.getTaskType();
            if ("SITE_PERMIT".equals(type)) {
                vo.setSitePermit(vo.getSitePermit() + 1);
            } else if ("MONITOR".equals(type)) {
                vo.setMonitor(vo.getMonitor() + 1);
            } else if ("ACCEPTANCE".equals(type)) {
                vo.setAcceptance(vo.getAcceptance() + 1);
            } else if ("ALARM_FEEDBACK".equals(type)) {
                vo.setAlarm(vo.getAlarm() + 1);
            }
        }
        return vo;
    }
}
