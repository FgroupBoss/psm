package com.fgroupboss.ai.psm.location.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GateAccessRecordVO {

    private Long id;
    private Long tenantId;
    private String gateCode;
    private String cardNo;
    private String tagNo;
    private Long personId;
    private String direction;
    private LocalDateTime accessTime;
    private String accessResult;
}
