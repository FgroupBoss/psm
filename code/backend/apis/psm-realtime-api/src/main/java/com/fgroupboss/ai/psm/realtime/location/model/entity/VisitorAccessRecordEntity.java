package com.fgroupboss.ai.psm.realtime.location.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("visitor_access_record")
public class VisitorAccessRecordEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private String visitorName;
    private String idCardNo;
    private String companyName;
    private Long hostPersonId;
    private String gateCode;
    private String tagNo;
    private String visitPurpose;
    private String direction;
    private LocalDateTime accessTime;
    private String accessResult;
    private LocalDateTime createdAt;
}
