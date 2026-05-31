package com.fgroupboss.ai.psm.risk.dualprevention.model.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RiskUnitTreeNodeVO {

    private Long id;
    private Long parentId;
    private String unitCode;
    private String unitName;
    private String inherentRiskLevel;
    private String residualRiskLevel;
    private String status;
    private List<RiskEventTreeNodeVO> events = new ArrayList<RiskEventTreeNodeVO>();
    private List<RiskUnitTreeNodeVO> children = new ArrayList<RiskUnitTreeNodeVO>();
}
