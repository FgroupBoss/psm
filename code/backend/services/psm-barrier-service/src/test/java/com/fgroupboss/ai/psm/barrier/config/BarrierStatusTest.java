package com.fgroupboss.ai.psm.barrier.config;

import com.fgroupboss.ai.psm.common.BusinessException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BarrierStatusTest {
    @Test void normalDegradesToDegraded() {
        assertEquals("DEGRADED", BarrierStatus.targetAfterDegrade("NORMAL"));
    }
    @Test void disabledCannotRestoreToNormalDirectly() {
        assertThrows(BusinessException.class, () -> BarrierStatus.assertDirectTransition("DISABLED", "NORMAL"));
    }
}
