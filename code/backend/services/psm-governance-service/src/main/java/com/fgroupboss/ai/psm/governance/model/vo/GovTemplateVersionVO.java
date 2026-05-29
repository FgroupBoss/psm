package com.fgroupboss.ai.psm.governance.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 模板版本视图。
 */
@Data
public class GovTemplateVersionVO {

    private Long id;
    private Long tenantId;
    private Long templateId;
    private String versionNo;
    private String content;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
