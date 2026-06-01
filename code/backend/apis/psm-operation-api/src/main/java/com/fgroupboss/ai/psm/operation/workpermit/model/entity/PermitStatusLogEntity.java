package com.fgroupboss.ai.psm.operation.workpermit.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("permit_status_log")
public class PermitStatusLogEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long workPermitId;
    private String fromStatus;
    private String toStatus;
    private String action;
    private String remark;
    private String operatorName;
    private Date operatedAt;
}
