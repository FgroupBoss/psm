package com.fgroupboss.ai.psm.inspection.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SignRecordVO {

    private Long id;
    private Long taskId;
    private Long routePointId;
    private String signType;
    private String signCode;
    private LocalDateTime signedAt;
    private Long operatorId;
    private String operatorName;
}
