package com.fgroupboss.ai.psm.workpermit.config;

import com.fgroupboss.ai.psm.common.BusinessException;

/**
 * 状态机合法流转定义；非法流转抛出 409。
 */
public final class WorkPermitStatusTransition {

    private WorkPermitStatusTransition() {
    }

    public static WorkPermitStatus submitTarget(WorkPermitStatus current) {
        if (current == WorkPermitStatus.DRAFT || current == WorkPermitStatus.RETURNED) {
            return WorkPermitStatus.APPROVING;
        }
        throw conflict("当前状态不允许提交审批");
    }

    public static WorkPermitStatus approveTarget(WorkPermitStatus current) {
        if (current == WorkPermitStatus.APPROVING) {
            return WorkPermitStatus.PENDING_SITE_PERMIT;
        }
        throw conflict("当前状态不允许审批通过");
    }

    public static WorkPermitStatus returnTarget(WorkPermitStatus current) {
        if (current == WorkPermitStatus.APPROVING) {
            return WorkPermitStatus.RETURNED;
        }
        throw conflict("当前状态不允许退回");
    }

    public static WorkPermitStatus rejectTarget(WorkPermitStatus current) {
        if (current == WorkPermitStatus.APPROVING) {
            return WorkPermitStatus.CLOSED;
        }
        throw conflict("当前状态不允许驳回");
    }

    public static WorkPermitStatus sitePermitTarget(WorkPermitStatus current) {
        if (current == WorkPermitStatus.PENDING_SITE_PERMIT) {
            return WorkPermitStatus.IN_PROGRESS;
        }
        throw conflict("当前状态不允许现场许可");
    }

    public static WorkPermitStatus suspendTarget(WorkPermitStatus current) {
        if (current == WorkPermitStatus.IN_PROGRESS) {
            return WorkPermitStatus.SUSPENDED;
        }
        throw conflict("当前状态不允许暂停");
    }

    public static WorkPermitStatus resumeTarget(WorkPermitStatus current) {
        if (current == WorkPermitStatus.SUSPENDED) {
            return WorkPermitStatus.IN_PROGRESS;
        }
        throw conflict("当前状态不允许恢复");
    }

    public static WorkPermitStatus requestAcceptanceTarget(WorkPermitStatus current) {
        if (current == WorkPermitStatus.IN_PROGRESS) {
            return WorkPermitStatus.PENDING_ACCEPTANCE;
        }
        throw conflict("当前状态不允许提交验收");
    }

    public static WorkPermitStatus acceptanceTarget(WorkPermitStatus current) {
        if (current == WorkPermitStatus.PENDING_ACCEPTANCE) {
            return WorkPermitStatus.CLOSED;
        }
        throw conflict("当前状态不允许验收关闭");
    }

    public static WorkPermitStatus terminateTarget(WorkPermitStatus current) {
        if (current == WorkPermitStatus.IN_PROGRESS || current == WorkPermitStatus.SUSPENDED) {
            return WorkPermitStatus.CLOSED;
        }
        throw conflict("当前状态不允许强制终止");
    }

    private static BusinessException conflict(String message) {
        return new BusinessException(409, message);
    }
}
