package com.fgroupboss.ai.psm.notification.support;

import com.fgroupboss.ai.psm.common.BusinessException;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * 一期内置模板渲染；后续可改为读取 config-rule 通知模板。
 */
public final class NotificationTemplateSupport {

    private NotificationTemplateSupport() {
    }

    public static RenderedTemplate render(String templateCode, Map<String, String> variables) {
        if (!StringUtils.hasText(templateCode)) {
            throw new BusinessException(400, "templateCode is required");
        }
        String code = templateCode.trim().toUpperCase();
        if ("ALARM_ESCALATION".equals(code)) {
            return new RenderedTemplate(
                    "报警升级提醒",
                    format("报警【{{alarmNo}}】因{{reason}}已升级至{{level}}，请及时处理。",
                            variables));
        }
        if ("CERT_EXPIRE_WARN".equals(code)) {
            return new RenderedTemplate(
                    "证书到期提醒",
                    format("人员【{{workerName}}】证书【{{certType}}】将于{{validTo}}到期，请安排复审。",
                            variables));
        }
        if ("WKP_TIMEOUT_WARN".equals(code)) {
            return new RenderedTemplate(
                    "作业超时提醒",
                    format("作业票【{{permitNo}}】已超过计划结束时间，当前状态{{status}}，请核实是否延期或关闭。",
                            variables));
        }
        if ("HAZARD_OVERDUE".equals(code)) {
            return new RenderedTemplate(
                    "隐患逾期提醒",
                    format("隐患【{{hazardNo}}】已逾期，当前状态{{status}}，请尽快处理。",
                            variables));
        }
        if ("HAZARD_ESCALATION".equals(code)) {
            return new RenderedTemplate(
                    "隐患升级提醒",
                    format("隐患【{{hazardNo}}】已升级至{{level}}，原因：{{reason}}，请及时跟进。",
                            variables));
        }
        throw new BusinessException(400, "unknown notification template: " + templateCode);
    }

    private static String format(String pattern, Map<String, String> variables) {
        String result = pattern;
        if (variables == null || variables.isEmpty()) {
            return result;
        }
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue() == null ? "" : entry.getValue();
            result = result.replace("{{" + key + "}}", value);
        }
        return result;
    }

    public static final class RenderedTemplate {
        private final String title;
        private final String content;

        public RenderedTemplate(String title, String content) {
            this.title = title;
            this.content = content;
        }

        public String getTitle() {
            return title;
        }

        public String getContent() {
            return content;
        }
    }
}
