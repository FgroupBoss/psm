package com.fgroupboss.ai.psm.processsafety.moc.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("moc_document_update")
public class MocDocumentUpdateEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long changeId;
    private String docType;
    private String docName;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer deleted;
}
