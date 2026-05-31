package com.fgroupboss.ai.psm.realtime.location.model.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class LocTrackPointVO {

    private Long id;
    private String tagNo;
    private Long personId;
    private Long areaId;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private LocalDateTime recordedAt;
}
