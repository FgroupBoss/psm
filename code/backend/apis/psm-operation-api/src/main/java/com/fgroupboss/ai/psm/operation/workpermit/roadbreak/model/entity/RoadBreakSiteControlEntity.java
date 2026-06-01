package com.fgroupboss.ai.psm.operation.workpermit.roadbreak.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("road_break_site_control")
public class RoadBreakSiteControlEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long workPermitId;
    private String itemCode;
    private String itemName;
    private String position;
    private String checkResult;
    private String attachmentRef;
    private String checkedBy;
    private Date checkedAt;
}
