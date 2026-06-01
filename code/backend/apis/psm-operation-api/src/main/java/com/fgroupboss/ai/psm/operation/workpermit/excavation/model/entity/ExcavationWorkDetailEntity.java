package com.fgroupboss.ai.psm.operation.workpermit.excavation.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("excavation_work_detail")
public class ExcavationWorkDetailEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long workPermitId;
    private String areaGeoJson;
    private BigDecimal depthM;
    private BigDecimal areaM2;
    private String method;
    private String drawingRef;
    private Date validUntil;
    private Date createdAt;
    private Date updatedAt;
    private Integer deleted;
}
