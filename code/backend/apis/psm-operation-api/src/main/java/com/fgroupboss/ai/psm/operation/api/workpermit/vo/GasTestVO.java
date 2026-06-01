package com.fgroupboss.ai.psm.operation.api.workpermit.vo;

import lombok.Data;

import java.util.Date;

@Data
public class GasTestVO {

    private Long id;
    private String gasName;
    private String measuredValue;
    private String unit;
    private Boolean qualified;
    private Date testedAt;
    private String testerName;
    private String remark;
}

