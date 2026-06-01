package com.fgroupboss.ai.psm.operation.api.workpermit.dto;

import lombok.Data;

@Data
public class PermitActionRequest {

    private String opinion;
    private String reason;
    /** 多节点审批任务 ID */
    private Long approvalTaskId;
    /** APPROVE / RETURN / REJECT */
    private String action;
    /** 由 Controller 从请求头注入，用于多节点审批人校验 */
    private Long operatorUserId;
}

