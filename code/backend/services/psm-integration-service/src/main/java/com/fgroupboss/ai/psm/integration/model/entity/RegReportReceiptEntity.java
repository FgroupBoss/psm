package com.fgroupboss.ai.psm.integration.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("reg_report_receipt")
public class RegReportReceiptEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long taskId;
    private Integer success;
    private String platformCode;
    private String platformMessage;
    private LocalDateTime receiptTime;
    private LocalDateTime createdAt;
}
