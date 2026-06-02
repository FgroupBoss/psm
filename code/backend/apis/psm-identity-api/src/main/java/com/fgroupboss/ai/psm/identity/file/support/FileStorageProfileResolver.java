package com.fgroupboss.ai.psm.identity.file.support;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.identity.file.config.FileProperties;
import com.fgroupboss.ai.psm.identity.file.config.StorageBackend;
import com.fgroupboss.ai.psm.identity.file.mapper.FileStorageProfileMapper;
import com.fgroupboss.ai.psm.identity.file.model.entity.FileStorageProfileEntity;
import com.fgroupboss.ai.psm.identity.file.storage.StorageProfileConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class FileStorageProfileResolver {

    private final FileStorageProfileMapper profileMapper;
    private final FileProperties fileProperties;
    private final ObjectMapper objectMapper;

    public ResolvedProfile resolve(Long tenantId, String profileCode) {
        FileStorageProfileEntity entity = null;
        if (StringUtils.hasText(profileCode)) {
            entity = profileMapper.selectOne(new LambdaQueryWrapper<FileStorageProfileEntity>()
                    .eq(FileStorageProfileEntity::getTenantId, tenantId)
                    .eq(FileStorageProfileEntity::getProfileCode, profileCode.trim())
                    .eq(FileStorageProfileEntity::getEnabled, 1)
                    .last("limit 1"));
            if (entity == null) {
                throw new BusinessException(404, "storage profile not found: " + profileCode);
            }
        } else {
            entity = profileMapper.selectOne(new LambdaQueryWrapper<FileStorageProfileEntity>()
                    .eq(FileStorageProfileEntity::getTenantId, tenantId)
                    .eq(FileStorageProfileEntity::getIsDefault, 1)
                    .eq(FileStorageProfileEntity::getEnabled, 1)
                    .last("limit 1"));
        }
        if (entity != null) {
            return toResolved(entity);
        }
        ResolvedProfile fallback = new ResolvedProfile();
        fallback.setStorageBackend(fileProperties.getDefaultBackend());
        fallback.setConfig(new StorageProfileConfig());
        fallback.getConfig().setStorageRoot(fileProperties.getStorageRoot());
        return fallback;
    }

    public StorageProfileConfig loadConfigForEntity(FileStorageProfileEntity entity) {
        if (entity == null) {
            return new StorageProfileConfig();
        }
        return StorageProfileConfig.fromJson(entity.getConfigJson(), objectMapper);
    }

    public StorageProfileConfig loadConfigById(Long profileId) {
        if (profileId == null) {
            return new StorageProfileConfig();
        }
        FileStorageProfileEntity entity = profileMapper.selectById(profileId);
        return entity == null ? new StorageProfileConfig() : loadConfigForEntity(entity);
    }

    public static final class ResolvedProfile {
        private Long profileId;
        private String storageBackend;
        private StorageProfileConfig config;

        public Long getProfileId() {
            return profileId;
        }

        public void setProfileId(Long profileId) {
            this.profileId = profileId;
        }

        public String getStorageBackend() {
            return storageBackend;
        }

        public void setStorageBackend(String storageBackend) {
            this.storageBackend = storageBackend;
        }

        public StorageProfileConfig getConfig() {
            return config;
        }

        public void setConfig(StorageProfileConfig config) {
            this.config = config;
        }

        public String resolveBucket() {
            if (config != null && StringUtils.hasText(config.getBucket())) {
                return config.getBucket();
            }
            return "";
        }

        public boolean preferPresigned() {
            if (StorageBackend.LOCAL.equals(storageBackend)) {
                return false;
            }
            if (config != null && config.isHttpMode()) {
                return true;
            }
            return StorageBackend.usesS3Api(storageBackend);
        }
    }

    private ResolvedProfile toResolved(FileStorageProfileEntity entity) {
        ResolvedProfile resolved = new ResolvedProfile();
        resolved.setProfileId(entity.getId());
        resolved.setStorageBackend(entity.getStorageBackend());
        resolved.setConfig(loadConfigForEntity(entity));
        return resolved;
    }
}
