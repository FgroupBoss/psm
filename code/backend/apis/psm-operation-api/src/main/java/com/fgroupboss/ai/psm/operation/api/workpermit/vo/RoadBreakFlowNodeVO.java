package com.fgroupboss.ai.psm.operation.api.workpermit.vo;

import lombok.Data;

@Data
public class RoadBreakFlowNodeVO {

    private String nodeCode;
    private String nodeName;
    private boolean current;
    private String status;
}
