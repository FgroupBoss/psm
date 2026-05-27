package com.fgroupboss.ai.psm.report.model.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class TrendSeriesVO {

    private String metric;
    private int days;
    private List<TrendPointVO> points = new ArrayList<TrendPointVO>();
    private Date dataRefreshedAt;
    private String dataSource;
}
