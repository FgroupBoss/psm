package com.fgroupboss.ai.psm.identity.file.storage;

import java.io.InputStream;

/**
 * 存储后端适配器：写入、读取、预签名与连通性探针。
 */
public interface StorageAdapter {

    String backend();

    StoredObject store(StoreContext ctx, StorageProfileConfig config, InputStream inputStream, String fileName);

    InputStream open(StoredObjectRef ref);

    PresignedUrl presignGet(StoredObjectRef ref, int ttlSeconds);

    HealthCheckResult testConnection(String backend, StorageProfileConfig config);
}
