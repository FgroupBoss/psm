package com.fgroupboss.ai.psm.workpermit.config;

import com.fgroupboss.ai.psm.common.BusinessException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WorkPermitStatusTransitionTest {

    @Test
    void submitShouldAllowDraftAndReturned() {
        assertEquals(WorkPermitStatus.APPROVING, WorkPermitStatusTransition.submitTarget(WorkPermitStatus.DRAFT));
        assertEquals(WorkPermitStatus.APPROVING, WorkPermitStatusTransition.submitTarget(WorkPermitStatus.RETURNED));
    }

    @Test
    void submitShouldRejectIllegalStatus() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> WorkPermitStatusTransition.submitTarget(WorkPermitStatus.APPROVING));
        assertEquals(409, ex.getCode());
    }

    @Test
    void approveShouldOnlyAllowApproving() {
        assertEquals(WorkPermitStatus.PENDING_SITE_PERMIT,
                WorkPermitStatusTransition.approveTarget(WorkPermitStatus.APPROVING));
        assertThrows(BusinessException.class,
                () -> WorkPermitStatusTransition.approveTarget(WorkPermitStatus.DRAFT));
    }

    @Test
    void returnShouldOnlyAllowApproving() {
        assertEquals(WorkPermitStatus.RETURNED,
                WorkPermitStatusTransition.returnTarget(WorkPermitStatus.APPROVING));
        assertThrows(BusinessException.class,
                () -> WorkPermitStatusTransition.returnTarget(WorkPermitStatus.DRAFT));
    }

    @Test
    void rejectShouldCloseFromApproving() {
        assertEquals(WorkPermitStatus.CLOSED,
                WorkPermitStatusTransition.rejectTarget(WorkPermitStatus.APPROVING));
    }

    @Test
    void sitePermitShouldMoveToInProgress() {
        assertEquals(WorkPermitStatus.IN_PROGRESS,
                WorkPermitStatusTransition.sitePermitTarget(WorkPermitStatus.PENDING_SITE_PERMIT));
    }

    @Test
    void suspendAndResumeShouldRoundTrip() {
        assertEquals(WorkPermitStatus.SUSPENDED,
                WorkPermitStatusTransition.suspendTarget(WorkPermitStatus.IN_PROGRESS));
        assertEquals(WorkPermitStatus.IN_PROGRESS,
                WorkPermitStatusTransition.resumeTarget(WorkPermitStatus.SUSPENDED));
    }

    @Test
    void acceptanceFlowShouldReachClosed() {
        assertEquals(WorkPermitStatus.PENDING_ACCEPTANCE,
                WorkPermitStatusTransition.requestAcceptanceTarget(WorkPermitStatus.IN_PROGRESS));
        assertEquals(WorkPermitStatus.CLOSED,
                WorkPermitStatusTransition.acceptanceTarget(WorkPermitStatus.PENDING_ACCEPTANCE));
    }

    @Test
    void terminateShouldCloseFromInProgressOrSuspended() {
        assertEquals(WorkPermitStatus.CLOSED,
                WorkPermitStatusTransition.terminateTarget(WorkPermitStatus.IN_PROGRESS));
        assertEquals(WorkPermitStatus.CLOSED,
                WorkPermitStatusTransition.terminateTarget(WorkPermitStatus.SUSPENDED));
        assertThrows(BusinessException.class,
                () -> WorkPermitStatusTransition.terminateTarget(WorkPermitStatus.DRAFT));
    }
}
