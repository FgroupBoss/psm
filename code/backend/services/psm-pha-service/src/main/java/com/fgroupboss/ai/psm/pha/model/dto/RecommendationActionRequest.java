package com.fgroupboss.ai.psm.pha.model.dto;

import lombok.Data;

@Data
public class RecommendationActionRequest {
    private Long ownerUserId;
    private String remark;
    private Boolean passed;
}
