package com.fgroupboss.ai.psm.contractor.config;

import com.fgroupboss.ai.psm.common.BusinessException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CompanyStatusTest {

    @Test
    void draftCannotJumpToApproved() {
        assertThrows(BusinessException.class, () -> CompanyStatus.assertDirectTransition("DRAFT", "APPROVED"));
    }

    @Test
    void draftCannotBeApprovedDirectly() {
        assertThrows(BusinessException.class, () -> CompanyStatus.assertApprove("DRAFT", true));
    }

    @Test
    void submitFromDraftGoesToPendingReview() {
        assertEquals("PENDING_REVIEW", CompanyStatus.targetAfterSubmit("DRAFT"));
    }

    @Test
    void approvePassFromPendingReview() {
        assertEquals("APPROVED", CompanyStatus.targetAfterApprove(true));
    }

    @Test
    void suspendOnlyFromApproved() {
        assertThrows(BusinessException.class, () -> CompanyStatus.assertSuspend("DRAFT"));
        CompanyStatus.assertSuspend("APPROVED");
    }
}
