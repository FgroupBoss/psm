package com.fgroupboss.ai.psm.mobile.client.vo;

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
