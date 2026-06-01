package com.fgroupboss.ai.psm.operation.api.workpermit.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RoadBreakFlowProgressVO {

    private String permitStatus;
    private Boolean trafficPlanConfirmed;
    private List<RoadBreakFlowNodeVO> nodes = new ArrayList<RoadBreakFlowNodeVO>();
}
