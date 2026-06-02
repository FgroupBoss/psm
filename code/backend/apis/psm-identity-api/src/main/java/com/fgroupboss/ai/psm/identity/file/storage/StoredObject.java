package com.fgroupboss.ai.psm.identity.file.storage;

import lombok.Data;

@Data
public class StoredObject {

    private String storageBackend;
    private String bucket;
    private String objectKey;
    /** LOCAL 时与 objectKey 相同 */
    private String storagePath;
    private long sizeBytes;
    private String sha256;
    private String region;
}
