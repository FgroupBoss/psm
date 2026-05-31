package com.fgroupboss.ai.psm.incidentgovernance.api.dto;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 治理指标 DTO — 用于跨站点治理看板。
 */
public class GovernanceMetricDTO implements Serializable {

    private Long id;
    private Long tenantId;
    private String metricCode;
    private String metricName;
    private String metricCategory;
    private BigDecimal currentValue;
    private BigDecimal targetValue;
    private String unit;
    private String period;
    private String trend;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getTenantId() { return tenantId; }
    public void setTenantId(Long tenantId) { this.tenantId = tenantId; }
    public String getMetricCode() { return metricCode; }
    public void setMetricCode(String metricCode) { this.metricCode = metricCode; }
    public String getMetricName() { return metricName; }
    public void setMetricName(String metricName) { this.metricName = metricName; }
    public String getMetricCategory() { return metricCategory; }
    public void setMetricCategory(String metricCategory) { this.metricCategory = metricCategory; }
    public BigDecimal getCurrentValue() { return currentValue; }
    public void setCurrentValue(BigDecimal currentValue) { this.currentValue = currentValue; }
    public BigDecimal getTargetValue() { return targetValue; }
    public void setTargetValue(BigDecimal targetValue) { this.targetValue = targetValue; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public String getPeriod() { return period; }
    public void setPeriod(String period) { this.period = period; }
    public String getTrend() { return trend; }
    public void setTrend(String trend) { this.trend = trend; }
}
