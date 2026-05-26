package com.fgroupboss.ai.psm.contractor.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("contractor_company")
public class ContractorCompanyEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private String companyCode;
    private String companyName;
    private String contactName;
    private String contactPhone;
    private String businessScope;
    private String status;
    private Integer blacklistFlag;
    private String remark;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer deleted;
}
