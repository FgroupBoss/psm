package com.fgroupboss.ai.psm.dualprevention.model.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class HazardAreaOpenCheckVO {

    private boolean hasBlocking;
    private int count;
    private List<HazardReportVO> hazards = new ArrayList<HazardReportVO>();
}
