package com.fgroupboss.ai.psm.processsafety.pssr.model.vo;

import lombok.Data;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Data
public class PssrCheckItemVO {
    private Long id;
    private Long tenantId;
    private Long templateId;
    private String itemNo;
    private String itemDesc;
    private String discipline;
    private String issueLevel;
    private Integer startupBlockFlag;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
