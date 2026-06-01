package com.fgroupboss.ai.psm.incidentgovernance.integration.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("reg_platform_config")
public class RegPlatformConfigEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private String platformCode;
    private String platformName;
    private String baseUrl;
    private String authType;
    private String credentialRef;
    private Integer enabled;
    private String remark;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer deleted;
}
