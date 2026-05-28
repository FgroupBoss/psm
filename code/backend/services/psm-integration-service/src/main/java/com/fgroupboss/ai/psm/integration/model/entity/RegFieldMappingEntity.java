package com.fgroupboss.ai.psm.integration.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("reg_field_mapping")
public class RegFieldMappingEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private String platformCode;
    private String dataDomain;
    private String sourceField;
    private String targetField;
    private String transformRule;
    private Integer enabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer deleted;
}
