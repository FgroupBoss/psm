package com.fgroupboss.ai.psm.operation.workbench.model.vo;

import lombok.Data;

/** 工作台待办按任务类型聚合的数量。 */
@Data
public class WorkbenchTodoCountVO {

    private int total;
    private int sitePermit;
    private int monitor;
    private int acceptance;
    private int alarm;
}
