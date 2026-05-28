package com.fgroupboss.ai.psm.location.model.vo;

import lombok.Data;

@Data
public class AreaHeadcountVO {

    private Long tenantId;
    private Long areaId;
    private int headcount;
    private int onlineCount;
}
