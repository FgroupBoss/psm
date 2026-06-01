package com.fgroupboss.ai.psm.operation.workpermit.blindplate.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("blind_plate_action_record")
public class BlindPlateActionRecordEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long workPermitId;
    private Long blindPlateId;
    private String fromStatus;
    private String toStatus;
    private String attachmentRef;
    private String operatedBy;
    private Date operatedAt;
}
