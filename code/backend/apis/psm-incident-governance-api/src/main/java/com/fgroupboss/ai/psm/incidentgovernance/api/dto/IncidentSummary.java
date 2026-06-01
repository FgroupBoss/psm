package com.fgroupboss.ai.psm.incidentgovernance.api.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 事故摘要 — 用于治理看板、报表等跨服务查询。
 */
public class IncidentSummary implements Serializable {

    private Long id;
    private Long tenantId;
    private String incidentCode;
    private String title;
    private String incidentType;
    private String severity;
    private String status;
    private LocalDateTime occurredAt;
    private Long areaId;
    private String areaName;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getTenantId() { return tenantId; }
    public void setTenantId(Long tenantId) { this.tenantId = tenantId; }
    public String getIncidentCode() { return incidentCode; }
    public void setIncidentCode(String incidentCode) { this.incidentCode = incidentCode; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getIncidentType() { return incidentType; }
    public void setIncidentType(String incidentType) { this.incidentType = incidentType; }
    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getOccurredAt() { return occurredAt; }
    public void setOccurredAt(LocalDateTime occurredAt) { this.occurredAt = occurredAt; }
    public Long getAreaId() { return areaId; }
    public void setAreaId(Long areaId) { this.areaId = areaId; }
    public String getAreaName() { return areaName; }
    public void setAreaName(String areaName) { this.areaName = areaName; }
}
