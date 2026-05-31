package com.fgroupboss.ai.psm.operation.workpermit.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("permit_site_confirm")
public class PermitSiteConfirmEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long workPermitId;
    private String confirmType;
    private String confirmContent;
    private String locationText;
    private String scanCode;
    private String terminalId;
    private String operatorName;
    private Date confirmedAt;
}
