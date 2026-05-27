package com.fgroupboss.ai.psm.majorhazard.config;

import com.fgroupboss.ai.psm.common.BusinessException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HazardStatusTest {

    @Test
    void publishShouldOnlyAllowDraft() {
        assertDoesNotThrow(() -> HazardStatus.assertPublish(HazardStatus.DRAFT.name()));
        assertThrows(BusinessException.class, () -> HazardStatus.assertPublish(HazardStatus.PUBLISHED.name()));
    }

    @Test
    void statusChangeShouldRejectDraftTarget() {
        assertThrows(BusinessException.class, () ->
                HazardStatus.assertStatusChange(HazardStatus.PUBLISHED.name(), HazardStatus.DRAFT.name()));
    }

    @Test
    void statusChangeShouldAllowPublishedToSuspended() {
        assertDoesNotThrow(() ->
                HazardStatus.assertStatusChange(HazardStatus.PUBLISHED.name(), HazardStatus.SUSPENDED.name()));
    }
}
