package com.fgroupboss.ai.psm.identity.file.storage;

import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.identity.file.config.FileProperties;
import com.fgroupboss.ai.psm.identity.file.config.StorageBackend;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.DigestInputStream;
import java.security.MessageDigest;

/**
 * 本地磁盘存储适配器，根目录由 {@link com.fgroupboss.ai.psm.identity.file.config.FileProperties} 指定。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LocalStorageAdapter implements StorageAdapter {

    private final FileProperties fileProperties;
    private Path rootPath;

    @PostConstruct
    public void init() throws IOException {
        String root = fileProperties.getStorageRoot();
        rootPath = Paths.get(root).toAbsolutePath().normalize();
        Files.createDirectories(rootPath);
        log.info("local file storage root={}", rootPath);
    }

    @Override
    public String backend() {
        return StorageBackend.LOCAL;
    }

    @Override
    public StoredObject store(StoreContext ctx, StorageProfileConfig config, InputStream inputStream, String fileName) {
        String root = StringUtils.hasText(config.getStorageRoot()) ? config.getStorageRoot() : fileProperties.getStorageRoot();
        Path base = Paths.get(root).toAbsolutePath().normalize();
        try {
            Files.createDirectories(base);
        } catch (IOException ex) {
            throw new BusinessException(500, "local storage init failed");
        }
        String relative = ObjectKeySupport.buildObjectKey(ctx.getTenantId(), fileName);
        Path target = base.resolve(relative).normalize();
        if (!target.startsWith(base)) {
            throw new BusinessException(400, "invalid file path");
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            DigestInputStream digestStream = new DigestInputStream(inputStream, digest);
            Files.createDirectories(target.getParent());
            long size = Files.copy(digestStream, target, StandardCopyOption.REPLACE_EXISTING);
            StoredObject stored = new StoredObject();
            stored.setStorageBackend(StorageBackend.LOCAL);
            stored.setBucket("");
            stored.setObjectKey(relative);
            stored.setStoragePath(relative);
            stored.setSizeBytes(size);
            stored.setSha256(toHex(digest.digest()));
            return stored;
        } catch (Exception ex) {
            throw new BusinessException(500, "local file storage failed");
        }
    }

    @Override
    public InputStream open(StoredObjectRef ref) {
        Path base = resolveBase(ref);
        Path path = base.resolve(ref.getStoragePath() != null ? ref.getStoragePath() : ref.getObjectKey()).normalize();
        if (!path.startsWith(base) || !Files.exists(path)) {
            throw new BusinessException(404, "file not found on disk");
        }
        try {
            return Files.newInputStream(path);
        } catch (IOException ex) {
            throw new BusinessException(404, "file not found on disk");
        }
    }

    @Override
    public PresignedUrl presignGet(StoredObjectRef ref, int ttlSeconds) {
        throw new BusinessException(400, "local storage does not support presigned url");
    }

    @Override
    public HealthCheckResult testConnection(String backend, StorageProfileConfig config) {
        String root = StringUtils.hasText(config.getStorageRoot()) ? config.getStorageRoot() : fileProperties.getStorageRoot();
        try {
            Path base = Paths.get(root).toAbsolutePath().normalize();
            Files.createDirectories(base);
            Path probe = base.resolve(".healthcheck");
            Files.write(probe, "ok".getBytes("UTF-8"));
            Files.deleteIfExists(probe);
            return HealthCheckResult.ok("local storage writable");
        } catch (Exception ex) {
            return HealthCheckResult.failed(ex.getMessage());
        }
    }

    private Path resolveBase(StoredObjectRef ref) {
        StorageProfileConfig config = ref.getProfileConfig();
        String root = config != null && StringUtils.hasText(config.getStorageRoot())
                ? config.getStorageRoot() : fileProperties.getStorageRoot();
        return Paths.get(root).toAbsolutePath().normalize();
    }

    private String toHex(byte[] digest) {
        StringBuilder builder = new StringBuilder(digest.length * 2);
        for (byte value : digest) {
            builder.append(String.format("%02x", value));
        }
        return builder.toString();
    }
}
