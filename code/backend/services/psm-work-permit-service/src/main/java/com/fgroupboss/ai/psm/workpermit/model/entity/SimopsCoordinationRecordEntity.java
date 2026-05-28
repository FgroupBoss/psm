package com.fgroupboss.ai.psm.workpermit.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("simops_coordination_record")
public class SimopsCoordinationRecordEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long scanResultId;
    private Long workPermitId;
    private String decision;
    private String opinion;
    private String conditionsText;
    private String coordinatorName;
    private Date coordinatedAt;
}
