package com.fgroupboss.ai.psm.workpermit.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("simops_scan_result")
public class SimopsScanResultEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long workPermitId;
    private String scanStage;
    private Integer conflictCount;
    private String maxSeverity;
    private String finalAction;
    private Integer passed;
    private String suggestion;
    private Date scannedAt;
}
