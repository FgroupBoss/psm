package com.fgroupboss.ai.psm.pha.config;

import com.fgroupboss.ai.psm.common.BusinessException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PhaProjectStatusTest {

    @Test
    void draftCannotJumpToPublished() {
        assertThrows(BusinessException.class, () -> PhaProjectStatus.assertDirectTransition("DRAFT", "PUBLISHED"));
    }

    @Test
    void submitFromDraft() {
        assertEquals("PENDING_REVIEW", PhaProjectStatus.targetAfterSubmit("DRAFT"));
    }
}