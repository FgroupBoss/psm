package com.fgroupboss.ai.psm.location.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("loc_tag_binding")
public class LocTagBindingEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private String tagNo;
    private String personType;
    private Long personId;
    private Long contractorId;
    private LocalDateTime bindAt;
    private LocalDateTime unbindAt;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer deleted;
}
