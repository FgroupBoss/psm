package com.fgroupboss.ai.psm.dualprevention.model.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class HazardOverdueCheckVO {

    private int checkedCount;
    private int markedCount;
    private List<HazardReportVO> overdueHazards = new ArrayList<HazardReportVO>();
}
