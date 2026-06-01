package com.fgroupboss.ai.psm.operation.api.workpermit.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RoadBreakPreCheckResultVO {

    private boolean passed;
    private String checkPoint;
    private String ruleVersion;
    private List<RoadBreakPreCheckReasonVO> reasons = new ArrayList<RoadBreakPreCheckReasonVO>();
}
