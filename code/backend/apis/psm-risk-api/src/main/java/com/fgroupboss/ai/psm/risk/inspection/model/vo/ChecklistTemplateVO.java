package com.fgroupboss.ai.psm.risk.inspection.model.vo;

import lombok.Data;

import java.util.List;

@Data
public class ChecklistTemplateVO {

    private Long id;
    private Long tenantId;
    private String templateCode;
    private String templateName;
    private String category;
    private String status;
    private String remark;
    private List<ChecklistItemVO> items;
}
