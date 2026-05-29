package com.fgroupboss.ai.psm.governance.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("gov_site_mapping")
public class GovSiteMappingEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private String siteCode;
    private String siteName;
    private Long orgId;
    private Integer enabledFlag;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer deleted;
}
