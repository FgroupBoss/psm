package com.fgroupboss.ai.psm.contractor.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("contractor_worker")
public class ContractorWorkerEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long companyId;
    private String workerCode;
    private String name;
    private String idNoHash;
    private String phoneMasked;
    private String tradeType;
    private String accessStatus;
    private String trainingStatus;
    private String certificateStatus;
    private String gateCardNo;
    private String locationTagNo;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer deleted;
}
