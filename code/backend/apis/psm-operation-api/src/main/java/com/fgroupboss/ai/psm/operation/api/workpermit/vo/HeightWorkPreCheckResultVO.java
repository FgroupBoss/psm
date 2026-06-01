package com.fgroupboss.ai.psm.operation.api.workpermit.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class HeightWorkPreCheckResultVO {

    private boolean passed;
    private String checkPoint;
    private String ruleVersion;
    private Date weatherSampledAt;
    private List<HeightWorkPreCheckReasonVO> reasons = new ArrayList<HeightWorkPreCheckReasonVO>();
}
