package com.fgroupboss.ai.psm.pssr.config;

import com.fgroupboss.ai.psm.common.BusinessException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PssrProjectStatusTest {
    @Test void draftCannotApproveDirectly() {
        assertThrows(BusinessException.class, () -> PssrProjectStatus.assertDirectTransition("DRAFT", "APPROVED"));
    }
    @Test void approveStartup() {
        assertEquals("APPROVED", PssrProjectStatus.targetAfterApproveStartup());
    }
}
