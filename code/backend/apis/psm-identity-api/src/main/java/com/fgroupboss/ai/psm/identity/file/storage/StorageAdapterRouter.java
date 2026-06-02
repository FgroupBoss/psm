package com.fgroupboss.ai.psm.identity.file.storage;

import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.identity.file.config.StorageBackend;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
@RequiredArgsConstructor
public class StorageAdapterRouter {

    private final LocalStorageAdapter localStorageAdapter;
    private final MinioStorageAdapter minioStorageAdapter;
    private final HttpGatewayStorageAdapter httpGatewayStorageAdapter;

    public StoredObject store(String backend, StoreContext ctx, StorageProfileConfig config,
                              InputStream inputStream, String fileName) {
        if (config != null && config.isHttpMode()) {
            return httpGatewayStorageAdapter.store(backend, ctx, config, inputStream, fileName);
        }
        if (StorageBackend.LOCAL.equals(backend)) {
            return localStorageAdapter.store(ctx, config, inputStream, fileName);
        }
        if (StorageBackend.usesS3Api(backend)) {
            ctx.setStorageBackend(backend);
            return minioStorageAdapter.store(ctx, config, inputStream, fileName);
        }
        throw new BusinessException(400, "unsupported storage backend: " + backend);
    }

    public InputStream open(StoredObjectRef ref) {
        if (ref.getProfileConfig() != null && ref.getProfileConfig().isHttpMode()) {
            throw new BusinessException(400, "http gateway download use presign");
        }
        if (StorageBackend.LOCAL.equals(ref.getStorageBackend())) {
            return localStorageAdapter.open(ref);
        }
        if (StorageBackend.usesS3Api(ref.getStorageBackend())) {
            return minioStorageAdapter.open(ref);
        }
        throw new BusinessException(400, "unsupported storage backend");
    }

    public PresignedUrl presignGet(StoredObjectRef ref, int ttlSeconds) {
        if (ref.getProfileConfig() != null && ref.getProfileConfig().isHttpMode()) {
            return httpGatewayStorageAdapter.presignGet(ref.getStorageBackend(), ref, ttlSeconds);
        }
        if (StorageBackend.usesS3Api(ref.getStorageBackend())) {
            return minioStorageAdapter.presignGet(ref, ttlSeconds);
        }
        throw new BusinessException(400, "presign not supported for backend");
    }

    public HealthCheckResult testConnection(String backend, StorageProfileConfig config) {
        if (config != null && config.isHttpMode()) {
            return httpGatewayStorageAdapter.testConnection(backend, config);
        }
        if (StorageBackend.LOCAL.equals(backend)) {
            return localStorageAdapter.testConnection(backend, config);
        }
        if (StorageBackend.usesS3Api(backend)) {
            return minioStorageAdapter.testConnection(backend, config);
        }
        return HealthCheckResult.skipped("unknown backend");
    }
}
