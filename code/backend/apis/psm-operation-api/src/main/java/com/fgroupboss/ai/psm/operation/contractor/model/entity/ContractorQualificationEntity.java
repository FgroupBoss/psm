package com.fgroupboss.ai.psm.operation.contractor.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("contractor_qualification")
public class ContractorQualificationEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long companyId;
    private String qualType;
    private String qualName;
    private String qualNo;
    private LocalDate validFrom;
    private LocalDate validTo;
    private Integer coreFlag;
    private Long fileId;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer deleted;
}
