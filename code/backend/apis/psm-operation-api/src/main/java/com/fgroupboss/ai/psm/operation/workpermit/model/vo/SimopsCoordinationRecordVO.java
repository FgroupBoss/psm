package com.fgroupboss.ai.psm.operation.workpermit.model.vo;

import lombok.Data;

import java.util.Date;

@Data
public class SimopsCoordinationRecordVO {

    private Long id;
    private Long scanResultId;
    private Long workPermitId;
    private String decision;
    private String opinion;
    private String conditionsText;
    private String coordinatorName;
    private Date coordinatedAt;
}
