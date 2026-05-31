package com.fgroupboss.ai.psm.processsafety.pssr.model.vo;

import lombok.Data;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Data
public class PssrTemplateVO {
    private Long id;
    private Long tenantId;
    private String templateCode;
    private String templateName;
    private String unitType;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
