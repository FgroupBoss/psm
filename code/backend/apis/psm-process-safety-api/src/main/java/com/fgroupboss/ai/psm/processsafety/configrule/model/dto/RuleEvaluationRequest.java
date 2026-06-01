package com.fgroupboss.ai.psm.processsafety.configrule.model.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * 通用规则评估请求。
 */
public class RuleEvaluationRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;
    @NotBlank(message = "scene is required")
    private String scene;
    private String ruleCode;
    private Map<String, Object> facts;

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getScene() {
        return scene;
    }

    public void setScene(String scene) {
        this.scene = scene;
    }

    public String getRuleCode() {
        return ruleCode;
    }

    public void setRuleCode(String ruleCode) {
        this.ruleCode = ruleCode;
    }

    public Map<String, Object> getFacts() {
        return facts;
    }

    public void setFacts(Map<String, Object> facts) {
        this.facts = facts;
    }
}
