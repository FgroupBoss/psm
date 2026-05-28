package com.fgroupboss.ai.psm.location.model.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class LocRealtimeVO {

    private Long id;
    private Long tenantId;
    private String tagNo;
    private Long personId;
    private Long areaId;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String onlineStatus;
    private LocalDateTime lastSeenAt;
}
