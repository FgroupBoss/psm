package com.fgroupboss.ai.psm.location.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("loc_tag")
public class LocTagEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private String tagNo;
    private String tagType;
    private String vendorCode;
    private String status;
    private String remark;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer deleted;
}
