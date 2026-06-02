package com.fgroupboss.ai.psm.identity.file.storage;

import java.io.InputStream;

public interface StorageAdapter {

    String backend();

    StoredObject store(StoreContext ctx, StorageProfileConfig config, InputStream inputStream, String fileName);

    InputStream open(StoredObjectRef ref);

    PresignedUrl presignGet(StoredObjectRef ref, int ttlSeconds);

    HealthCheckResult testConnection(String backend, StorageProfileConfig config);
}
