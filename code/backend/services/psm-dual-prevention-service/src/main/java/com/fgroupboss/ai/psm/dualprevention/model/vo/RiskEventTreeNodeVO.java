package com.fgroupboss.ai.psm.dualprevention.model.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RiskEventTreeNodeVO {

    private Long id;
    private String eventCode;
    private String eventName;
    private String inherentRiskLevel;
    private String status;
    private List<ControlMeasureVO> measures = new ArrayList<ControlMeasureVO>();
}
