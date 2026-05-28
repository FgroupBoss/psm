package com.fgroupboss.ai.psm.workpermit.model.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class SimopsScanResultVO {

    private Long id;
    private Long tenantId;
    private Long workPermitId;
    private String scanStage;
    private Integer conflictCount;
    private String maxSeverity;
    private String finalAction;
    private Boolean passed;
    private String suggestion;
    private Date scannedAt;
    private List<SimopsConflictItemVO> items = new ArrayList<SimopsConflictItemVO>();
    private List<SimopsConflictReasonVO> reasons = new ArrayList<SimopsConflictReasonVO>();
}
