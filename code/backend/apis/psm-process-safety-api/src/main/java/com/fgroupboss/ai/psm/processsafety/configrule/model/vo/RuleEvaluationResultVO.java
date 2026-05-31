package com.fgroupboss.ai.psm.processsafety.configrule.model.vo;

import java.util.ArrayList;
import java.util.List;

/**
 * 规则评估结果。
 */
public class RuleEvaluationResultVO {

    private boolean passed;
    private String level;
    private String ruleCode;
    private String message;
    private List<String> evidence = new ArrayList<String>();
    private String suggestion;

    public boolean isPassed() {
        return passed;
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getRuleCode() {
        return ruleCode;
    }

    public void setRuleCode(String ruleCode) {
        this.ruleCode = ruleCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getEvidence() {
        return evidence;
    }

    public void setEvidence(List<String> evidence) {
        this.evidence = evidence;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }
}
