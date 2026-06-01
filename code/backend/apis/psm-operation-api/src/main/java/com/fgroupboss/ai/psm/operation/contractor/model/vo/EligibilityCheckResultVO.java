package com.fgroupboss.ai.psm.operation.contractor.model.vo;

import lombok.Data;

import java.util.List;

@Data
public class EligibilityCheckResultVO {

    private boolean passed;
    private List<EligibilityReasonVO> reasons;
}
