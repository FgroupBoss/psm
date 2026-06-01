package com.fgroupboss.ai.psm.operation.api.workpermit.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class HeightWorkFlowProgressVO {

    private String permitStatus;
    private String heightLevel;
    private String riskClass;
    private Date validUntil;
    private List<HeightWorkFlowNodeVO> nodes = new ArrayList<HeightWorkFlowNodeVO>();
}
