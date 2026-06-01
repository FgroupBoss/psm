package com.fgroupboss.ai.psm.risk.majorhazard.model.vo;

import lombok.Data;

@Data
public class HazardAttachmentVO {

    private Long id;
    private Long tenantId;
    private Long hazardId;
    private String attachmentType;
    private Long fileId;
    private String fileName;
}
