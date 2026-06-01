package com.fgroupboss.ai.psm.operation.api.workpermit.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ConfinedSpacePreCheckResultVO {

    private boolean passed;
    private String checkPoint;
    private String ruleVersion;
    private List<ConfinedSpacePreCheckReasonVO> reasons = new ArrayList<ConfinedSpacePreCheckReasonVO>();
}
