package com.fgroupboss.ai.psm.operation.workpermit.blindplate.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("blind_plate_registry")
public class BlindPlateRegistryEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private String blindPlateNo;
    private String pipelineId;
    private String position;
    private String diagramRef;
    private String spec;
    private String material;
    private String tagNo;
    private String status;
    private Integer versionNo;
    private Date createdAt;
    private Date updatedAt;
    private Integer deleted;
}
