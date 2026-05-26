package com.fgroupboss.ai.psm.contractor.config;

import com.fgroupboss.ai.psm.common.BusinessException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WorkerStatusTest {

    @Test
    void incompleteCannotJumpToApproved() {
        assertThrows(BusinessException.class, () -> WorkerStatus.assertDirectTransition("INCOMPLETE", "APPROVED"));
    }

    @Test
    void incompleteCannotBeApprovedDirectly() {
        assertThrows(BusinessException.class, () -> WorkerStatus.assertApprove("INCOMPLETE", true));
    }

    @Test
    void submitFromIncompleteGoesToPendingReview() {
        assertEquals("PENDING_REVIEW", WorkerStatus.targetAfterSubmit("INCOMPLETE"));
    }

    @Test
    void approvePassFromPendingReview() {
        assertEquals("APPROVED", WorkerStatus.targetAfterApprove(true));
    }

    @Test
    void suspendFromApprovedOrRestricted() {
        WorkerStatus.assertSuspend("APPROVED");
        WorkerStatus.assertSuspend("RESTRICTED");
        assertThrows(BusinessException.class, () -> WorkerStatus.assertSuspend("INCOMPLETE"));
    }
}
