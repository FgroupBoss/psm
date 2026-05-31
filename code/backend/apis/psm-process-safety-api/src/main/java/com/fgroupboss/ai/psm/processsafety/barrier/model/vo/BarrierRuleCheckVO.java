package com.fgroupboss.ai.psm.processsafety.barrier.model.vo;

import lombok.Data;

import java.util.List;

@Data
public class BarrierRuleCheckVO {

    private boolean passed;
    private int failedBarrierCount;
    private List<String> messages;
}
