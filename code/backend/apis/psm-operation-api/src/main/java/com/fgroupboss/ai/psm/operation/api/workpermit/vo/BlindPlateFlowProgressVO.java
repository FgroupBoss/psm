package com.fgroupboss.ai.psm.operation.api.workpermit.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BlindPlateFlowProgressVO {

    private String permitStatus;
    private String operationType;
    private Boolean actionConfirmed;
    private List<BlindPlateFlowNodeVO> nodes = new ArrayList<BlindPlateFlowNodeVO>();
}
