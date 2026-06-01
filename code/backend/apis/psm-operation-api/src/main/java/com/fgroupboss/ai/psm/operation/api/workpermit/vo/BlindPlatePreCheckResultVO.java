package com.fgroupboss.ai.psm.operation.api.workpermit.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BlindPlatePreCheckResultVO {

    private boolean passed;
    private String checkPoint;
    private String ruleVersion;
    private List<BlindPlatePreCheckReasonVO> reasons = new ArrayList<BlindPlatePreCheckReasonVO>();
}
