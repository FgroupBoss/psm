package com.fgroupboss.ai.psm.operation.workpermit.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("mobile_draft_sync")
public class MobileDraftSyncEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private String clientDraftId;
    private Long userId;
    private String draftType;
    private Long bizId;
    private String payloadJson;
    private String syncStatus;
    private String lastError;
    private Date createdAt;
    private Date updatedAt;
}
