package com.fgroupboss.ai.psm.risk.majorhazard.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("major_hazard_attachment")
public class MajorHazardAttachmentEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long hazardId;
    private String attachmentType;
    private Long fileId;
    private String fileName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer deleted;
}
