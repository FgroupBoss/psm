package com.fgroupboss.ai.psm.identity.file.storage;

import lombok.Data;

@Data
public class StoreContext {

    private Long tenantId;
    private String storageBackend;
    private Long storageProfileId;
    private String bucket;
    private String bizType;
    private String contentType;
    private long contentLength = -1;
}
