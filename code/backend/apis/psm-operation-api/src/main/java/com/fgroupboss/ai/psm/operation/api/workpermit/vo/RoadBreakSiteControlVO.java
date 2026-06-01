package com.fgroupboss.ai.psm.operation.api.workpermit.vo;

import lombok.Data;

import java.util.Date;

@Data
public class RoadBreakSiteControlVO {

    private Long id;
    private String itemCode;
    private String itemName;
    private String position;
    private String checkResult;
    private String attachmentRef;
    private String checkedBy;
    private Date checkedAt;
}
