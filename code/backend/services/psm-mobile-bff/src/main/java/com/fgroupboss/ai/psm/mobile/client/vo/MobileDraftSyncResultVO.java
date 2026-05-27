package com.fgroupboss.ai.psm.mobile.client.vo;

import lombok.Data;

import java.util.Date;

@Data
public class MobileDraftSyncResultVO {

    private String clientDraftId;
    private String syncStatus;
    private String storage;
    private String lastError;
    private Date syncedAt;
}
