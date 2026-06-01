package com.fgroupboss.ai.psm.processsafety.pssr.model.vo;

import lombok.Data;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Data
public class PssrExecutionRecordVO {
    private Long id;
    private Long tenantId;
    private Long projectId;
    private Long checkItemId;
    private String result;
    private String remark;
    private Long executorUserId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
