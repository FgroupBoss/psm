package com.fgroupboss.ai.psm.realtime.location.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("gate_access_record")
public class GateAccessRecordEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private String gateCode;
    private String cardNo;
    private String tagNo;
    private Long personId;
    private String direction;
    private LocalDateTime accessTime;
    private String accessResult;
    private LocalDateTime createdAt;
}
