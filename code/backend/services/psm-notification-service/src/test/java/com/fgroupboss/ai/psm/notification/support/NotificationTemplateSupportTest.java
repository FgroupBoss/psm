package com.fgroupboss.ai.psm.notification.support;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

class NotificationTemplateSupportTest {

    @Test
    void shouldRenderAlarmEscalationTemplate() {
        Map<String, String> vars = new HashMap<String, String>();
        vars.put("alarmNo", "A-001");
        vars.put("reason", "CONFIRM_TIMEOUT");
        vars.put("level", "L1");
        NotificationTemplateSupport.RenderedTemplate rendered =
                NotificationTemplateSupport.render("ALARM_ESCALATION", vars);
        assertTrue(rendered.getContent().contains("A-001"));
        assertTrue(rendered.getContent().contains("CONFIRM_TIMEOUT"));
    }
}
