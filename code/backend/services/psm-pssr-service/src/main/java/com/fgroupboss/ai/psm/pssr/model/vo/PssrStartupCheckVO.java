package com.fgroupboss.ai.psm.pssr.model.vo;

import lombok.Data;

import java.util.List;

@Data
public class PssrStartupCheckVO {

    private Long projectId;
    private boolean canApprove;
    private int openBlockingIssueCount;
    private List<String> blockingReasons;
}
