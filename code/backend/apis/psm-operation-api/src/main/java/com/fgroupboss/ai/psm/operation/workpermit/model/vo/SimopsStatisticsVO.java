package com.fgroupboss.ai.psm.operation.workpermit.model.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SimopsStatisticsVO {

    private Long totalScans;
    private Long totalConflicts;
    private Long blockCount;
    private Long coordinateCount;
    private Long warnCount;
    private List<SimopsTypePairStatVO> topTypePairs = new ArrayList<SimopsTypePairStatVO>();
    private List<SimopsAreaStatVO> topAreas = new ArrayList<SimopsAreaStatVO>();
}
