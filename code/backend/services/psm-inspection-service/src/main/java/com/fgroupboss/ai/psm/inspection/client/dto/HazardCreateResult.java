package com.fgroupboss.ai.psm.inspection.client.dto;

import lombok.Data;

/**
 * 双重预防隐患创建结果摘要。
 */
@Data
public class HazardCreateResult {

    private Long id;
    private Long tenantId;
    private String hazardNo;
    private String status;
}
