package com.fgroupboss.ai.psm.risk.majorhazard.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class RiskContextRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;
    private Long areaId;
    private Long unitId;
    private List<Long> pointIds;
    /** 是否检查区域活跃高等级报警阻断，默认 true。 */
    private Boolean checkBlockingAlarm;
}
