package com.fgroupboss.ai.psm.operation.workpermit.lifting.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("lifting_trial_record")
public class LiftingTrialRecordEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long workPermitId;
    private String trialResult;
    private String issueDesc;
    private String confirmedBy;
    private String attachmentRef;
    private Date trialAt;
    private Date createdAt;
}
