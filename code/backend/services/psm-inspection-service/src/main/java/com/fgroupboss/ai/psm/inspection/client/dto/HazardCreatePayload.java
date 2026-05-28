package com.fgroupboss.ai.psm.inspection.client.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 调用双重预防隐患上报接口的请求体。
 */
@Data
public class HazardCreatePayload {

    private Long tenantId;
    private String hazardLevel;
    private String sourceType;
    private Long sourceBizId;
    private Long riskUnitId;
    private Long areaId;
    private String description;
    private LocalDateTime foundAt;
    private LocalDateTime rectificationDeadline;
    private Long contractorId;
}
