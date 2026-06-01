package com.fgroupboss.ai.psm.operation.workpermit.blindplate.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("blind_plate_work_detail")
public class BlindPlateWorkDetailEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long workPermitId;
    private Long blindPlateId;
    private String operationType;
    private String pipelineId;
    private String positionDescription;
    private String positionDiagramRef;
    private String mediumName;
    private String temperature;
    private String pressure;
    private String hazardJson;
    private String tagNo;
    private String spec;
    private String material;
    private Integer actionConfirmed;
    private Date createdAt;
    private Date updatedAt;
    private Integer deleted;
}
