package com.fgroupboss.ai.psm.configrule.model.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * 配置项新增与更新请求。
 */
public class ConfigItemRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;
    @NotBlank(message = "configCode is required")
    private String configCode;
    @NotBlank(message = "configName is required")
    private String configName;
    private String bizScene;
    private Map<String, Object> content;
    private String remark;
    private String status;

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getConfigCode() {
        return configCode;
    }

    public void setConfigCode(String configCode) {
        this.configCode = configCode;
    }

    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public String getBizScene() {
        return bizScene;
    }

    public void setBizScene(String bizScene) {
        this.bizScene = bizScene;
    }

    public Map<String, Object> getContent() {
        return content;
    }

    public void setContent(Map<String, Object> content) {
        this.content = content;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
