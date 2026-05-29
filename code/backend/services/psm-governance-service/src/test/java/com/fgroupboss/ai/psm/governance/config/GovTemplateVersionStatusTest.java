package com.fgroupboss.ai.psm.governance.config;

import com.fgroupboss.ai.psm.common.BusinessException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GovTemplateVersionStatusTest {

    @Test
    void disabledCannotRepublish() {
        assertThrows(BusinessException.class,
                () -> GovTemplateVersionStatus.assertDirectTransition("DISABLED", "PUBLISHED"));
    }

    @Test
    void publishFromDraft() {
        assertEquals("PUBLISHED", GovTemplateVersionStatus.targetAfterPublish());
    }
}
