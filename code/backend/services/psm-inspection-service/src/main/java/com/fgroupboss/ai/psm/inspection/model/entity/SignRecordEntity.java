package com.fgroupboss.ai.psm.inspection.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("insp_sign_record")
public class SignRecordEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long taskId;
    private Long routePointId;
    private String signType;
    private String signCode;
    private LocalDateTime signedAt;
    private Long operatorId;
    private String operatorName;
    private LocalDateTime createdAt;
}
