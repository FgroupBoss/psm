package com.fgroupboss.ai.psm.pha.model.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PhaProjectReportVO {
    private PhaProjectVO project;
    private List<PhaNodeVO> nodes = new ArrayList<PhaNodeVO>();
    private List<PhaRecommendationVO> recommendations = new ArrayList<PhaRecommendationVO>();
    private List<LopaScenarioVO> lopaScenarios = new ArrayList<LopaScenarioVO>();
}
