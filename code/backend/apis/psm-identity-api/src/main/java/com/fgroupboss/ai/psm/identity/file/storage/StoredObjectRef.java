package com.fgroupboss.ai.psm.identity.file.storage;

import lombok.Data;

@Data
public class StoredObjectRef {

    private Long tenantId;
    private String storageBackend;
    private String bucket;
    private String objectKey;
    private String storagePath;
    private String fileName;
    private String contentType;
    private StorageProfileConfig profileConfig;
}
