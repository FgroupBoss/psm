package com.fgroupboss.ai.psm.workpermit.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("work_permit_worker")
public class WorkPermitWorkerEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long workPermitId;
    private String workerType;
    private Long workerId;
    private String workerName;
    private String roleCode;
    private Long companyId;
    private Date createdAt;
    private Integer deleted;
}
