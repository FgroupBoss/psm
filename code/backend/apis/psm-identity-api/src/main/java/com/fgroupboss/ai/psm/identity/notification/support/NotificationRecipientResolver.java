package com.fgroupboss.ai.psm.identity.notification.support;

import com.fgroupboss.ai.psm.identity.notification.model.dto.NotificationSendRequest;
import org.springframework.util.StringUtils;

import java.util.Map;

public final class NotificationRecipientResolver {

    private NotificationRecipientResolver() {
    }

    public static String resolvePhone(NotificationSendRequest request) {
        if (StringUtils.hasText(request.getRecipientPhone())) {
            return request.getRecipientPhone().trim();
        }
        return fromVariables(request.getVariables(), "phone", "mobile", "recipientPhone");
    }

    public static String resolveEmail(NotificationSendRequest request) {
        if (StringUtils.hasText(request.getRecipientEmail())) {
            return request.getRecipientEmail().trim();
        }
        return fromVariables(request.getVariables(), "email", "recipientEmail");
    }

    private static String fromVariables(Map<String, String> variables, String... keys) {
        if (variables == null || variables.isEmpty()) {
            return null;
        }
        for (String key : keys) {
            String value = variables.get(key);
            if (StringUtils.hasText(value)) {
                return value.trim();
            }
        }
        return null;
    }
}
