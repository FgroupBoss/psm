package com.fgroupboss.ai.psm.risk.api.dto;

import java.io.Serializable;

/**
 * 风险点摘要 — 用于报警域、作业域等跨服务风险查询。
 */
public class RiskPointSummary implements Serializable {

    private Long id;
    private Long tenantId;
    private String riskCode;
    private String riskName;
    private String riskLevel;
    private Long areaId;
    private String areaName;
    private String status;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getTenantId() { return tenantId; }
    public void setTenantId(Long tenantId) { this.tenantId = tenantId; }
    public String getRiskCode() { return riskCode; }
    public void setRiskCode(String riskCode) { this.riskCode = riskCode; }
    public String getRiskName() { return riskName; }
    public void setRiskName(String riskName) { this.riskName = riskName; }
    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }
    public Long getAreaId() { return areaId; }
    public void setAreaId(Long areaId) { this.areaId = areaId; }
    public String getAreaName() { return areaName; }
    public void setAreaName(String areaName) { this.areaName = areaName; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
