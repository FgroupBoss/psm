package com.fgroupboss.ai.psm.operation.client.dto;

import java.io.Serializable;

/**
 * 重大危险源摘要 — 用于实时感知域、作业域等跨服务查询。
 */
public class MajorHazardSummary implements Serializable {

    private Long id;
    private Long tenantId;
    private String hazardCode;
    private String hazardName;
    private String hazardLevel;
    private Long areaId;
    private String areaName;
    private String category;
    private String status;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getTenantId() { return tenantId; }
    public void setTenantId(Long tenantId) { this.tenantId = tenantId; }
    public String getHazardCode() { return hazardCode; }
    public void setHazardCode(String hazardCode) { this.hazardCode = hazardCode; }
    public String getHazardName() { return hazardName; }
    public void setHazardName(String hazardName) { this.hazardName = hazardName; }
    public String getHazardLevel() { return hazardLevel; }
    public void setHazardLevel(String hazardLevel) { this.hazardLevel = hazardLevel; }
    public Long getAreaId() { return areaId; }
    public void setAreaId(Long areaId) { this.areaId = areaId; }
    public String getAreaName() { return areaName; }
    public void setAreaName(String areaName) { this.areaName = areaName; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
