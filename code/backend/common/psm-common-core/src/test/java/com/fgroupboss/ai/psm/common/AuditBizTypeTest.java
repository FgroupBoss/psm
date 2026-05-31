package com.fgroupboss.ai.psm.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuditBizTypeTest {

    @Test
    void contractorTypesShouldUseContractorPrefix() {
        assertEquals("CONTRACTOR_COMPANY", AuditBizType.CONTRACTOR_COMPANY.code());
        assertTrue(AuditBizType.CONTRACTOR_WORKER.name().startsWith("CONTRACTOR_"));
    }

    @Test
    void hazardTypesShouldUseMajorHazardPrefix() {
        assertEquals("MAJOR_HAZARD", AuditBizType.MAJOR_HAZARD.code());
        assertTrue(AuditBizType.MAJOR_HAZARD_POINT.name().startsWith("MAJOR_HAZARD_"));
    }

    @Test
    void alarmTypesShouldUseAlarmPrefix() {
        assertEquals("ALARM", AuditBizType.ALARM.code());
        assertTrue(AuditBizType.ALARM_ACTION.name().startsWith("ALARM_"));
    }

    @Test
    void dualPreventionTypesShouldBeDefined() {
        assertEquals("DUAL_PREVENTION_HAZARD", AuditBizType.DUAL_PREVENTION_HAZARD.code());
        assertEquals("INSPECTION_TASK", AuditBizType.INSPECTION_TASK.code());
    }
}
