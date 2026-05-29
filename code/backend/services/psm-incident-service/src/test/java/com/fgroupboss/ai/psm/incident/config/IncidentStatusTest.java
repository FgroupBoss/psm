package com.fgroupboss.ai.psm.incident.config;

import com.fgroupboss.ai.psm.common.BusinessException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class IncidentStatusTest {

    @Test
    void reportedCannotCloseDirectly() {
        assertThrows(BusinessException.class,
                () -> IncidentStatus.assertDirectTransition("REPORTED", "CLOSED"));
    }

    @Test
    void startInvestigation() {
        assertEquals("INVESTIGATING", IncidentStatus.targetAfterStartInvestigation());
    }
}
