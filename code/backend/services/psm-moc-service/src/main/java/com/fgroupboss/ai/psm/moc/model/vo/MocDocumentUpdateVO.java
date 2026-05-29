package com.fgroupboss.ai.psm.moc.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MocDocumentUpdateVO {
    private Long id;
    private Long tenantId;
    private Long changeId;
    private String docType;
    private String docName;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
