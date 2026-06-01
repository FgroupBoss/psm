package com.fgroupboss.ai.psm.operation.workpermit.excavation.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("excavation_site_check")
public class ExcavationSiteCheckEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long workPermitId;
    private String stage;
    private String itemCode;
    private String itemName;
    private String checkResult;
    private String attachmentRef;
    private String checkedBy;
    private Date checkedAt;
}
