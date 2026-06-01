package com.fgroupboss.ai.psm.operation.api.workpermit.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ExcavationFlowProgressVO {

    private String permitStatus;
    private List<ExcavationFlowNodeVO> nodes = new ArrayList<ExcavationFlowNodeVO>();
}
