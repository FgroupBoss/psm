package com.fgroupboss.ai.psm.incidentgovernance.integration.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("reg_code_mapping")
public class RegCodeMappingEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private String platformCode;
    private String mappingType;
    private String sourceCode;
    private String targetCode;
    private String description;
    private Integer enabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer deleted;
}
