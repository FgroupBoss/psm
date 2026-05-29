package com.fgroupboss.ai.psm.moc.config;

import com.fgroupboss.ai.psm.common.BusinessException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MocChangeStatusTest {

    @Test
    void draftCannotCloseDirectly() {
        assertThrows(BusinessException.class, () -> MocChangeStatus.assertDirectTransition("DRAFT", "CLOSED"));
    }

    @Test
    void submitFromDraft() {
        assertEquals("SUBMITTED", MocChangeStatus.targetAfterSubmit());
    }

    @Test
    void onlyDraftOrReturnedEditable() {
        MocChangeStatus.assertEditable("DRAFT");
        MocChangeStatus.assertEditable("RETURNED");
        assertThrows(BusinessException.class, () -> MocChangeStatus.assertEditable("APPROVING"));
    }

    @Test
    void terminalCannotTransition() {
        assertThrows(BusinessException.class, () -> MocChangeStatus.assertDirectTransition("CLOSED", "DRAFT"));
    }
}
