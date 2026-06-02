package com.fgroupboss.ai.psm.identity.file.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.identity.file.config.StorageBackend;
import com.fgroupboss.ai.psm.identity.file.mapper.FileStorageProfileMapper;
import com.fgroupboss.ai.psm.identity.file.model.dto.FileStorageProfileRequest;
import com.fgroupboss.ai.psm.identity.file.model.entity.FileStorageProfileEntity;
import com.fgroupboss.ai.psm.identity.file.model.vo.FileBackendSchemaVO;
import com.fgroupboss.ai.psm.identity.file.model.vo.FileStorageProfileVO;
import com.fgroupboss.ai.psm.identity.file.service.FileStorageProfileService;
import com.fgroupboss.ai.psm.identity.file.storage.HealthCheckResult;
import com.fgroupboss.ai.psm.identity.file.storage.StorageAdapterRouter;
import com.fgroupboss.ai.psm.identity.file.storage.StorageProfileConfig;
import com.fgroupboss.ai.psm.identity.file.support.FileStorageProfileResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 租户存储配置档管理服务实现。
 *
 * <p>配置 JSON 落库时对密钥字段脱敏展示；探针经 {@link StorageAdapterRouter} 校验连通性。</p>
 */
@Service
@RequiredArgsConstructor
public class FileStorageProfileServiceImpl implements FileStorageProfileService {

    private static final List<String> SECRET_KEYS = Arrays.asList(
            "secretKey", "accessKeySecret", "apiSecret", "secret");

    private final FileStorageProfileMapper profileMapper;
    private final StorageAdapterRouter storageAdapterRouter;
    private final ObjectMapper objectMapper;

    @Override
    public List<FileStorageProfileVO> list(Long tenantId) {
        List<FileStorageProfileEntity> entities = profileMapper.selectList(
                new LambdaQueryWrapper<FileStorageProfileEntity>()
                        .eq(FileStorageProfileEntity::getTenantId, tenantId)
                        .orderByDesc(FileStorageProfileEntity::getIsDefault)
                        .orderByAsc(FileStorageProfileEntity::getProfileCode));
        List<FileStorageProfileVO> result = new ArrayList<FileStorageProfileVO>();
        for (FileStorageProfileEntity entity : entities) {
            result.add(toVo(entity, false));
        }
        return result;
    }

    @Override
    public FileStorageProfileVO getDefault(Long tenantId) {
        FileStorageProfileEntity entity = profileMapper.selectOne(new LambdaQueryWrapper<FileStorageProfileEntity>()
                .eq(FileStorageProfileEntity::getTenantId, tenantId)
                .eq(FileStorageProfileEntity::getIsDefault, 1)
                .eq(FileStorageProfileEntity::getEnabled, 1)
                .last("limit 1"));
        if (entity == null) {
            return null;
        }
        return toVo(entity, false);
    }

    /**
     * 实现方式：按 tenantId + profileCode 幂等保存，处理默认档互斥并脱敏返回。
     */
    @Override
    @Transactional
    public FileStorageProfileVO save(FileStorageProfileRequest request) {
        if (request == null || request.getTenantId() == null) {
            throw new BusinessException(400, "tenantId is required");
        }
        FileStorageProfileEntity existing = profileMapper.selectOne(new LambdaQueryWrapper<FileStorageProfileEntity>()
                .eq(FileStorageProfileEntity::getTenantId, request.getTenantId())
                .eq(FileStorageProfileEntity::getProfileCode, request.getProfileCode().trim())
                .last("limit 1"));
        StorageProfileConfig config = mapConfig(request.getConfig());
        FileStorageProfileEntity entity = existing != null ? existing : new FileStorageProfileEntity();
        entity.setTenantId(request.getTenantId());
        entity.setProfileCode(request.getProfileCode().trim());
        entity.setProfileName(request.getProfileName().trim());
        entity.setStorageBackend(request.getStorageBackend().trim().toUpperCase());
        mergeSecrets(existing, config);
        entity.setConfigJson(config.toJson(objectMapper));
        entity.setEnabled(Boolean.FALSE.equals(request.getEnabled()) ? 0 : 1);
        boolean asDefault = Boolean.TRUE.equals(request.getDefaultProfile());
        Date now = new Date();
        if (existing == null) {
            entity.setCreatedAt(now);
            entity.setUpdatedAt(now);
            profileMapper.insert(entity);
        } else {
            entity.setUpdatedAt(now);
            profileMapper.updateById(entity);
        }
        if (asDefault) {
            clearDefault(request.getTenantId(), entity.getId());
            entity.setIsDefault(1);
            profileMapper.updateById(entity);
        } else if (entity.getIsDefault() == null) {
            entity.setIsDefault(0);
            profileMapper.updateById(entity);
        }
        return toVo(entity, false);
    }

    /**
     * 实现方式：加载租户配置档后调用对应存储适配器做连通性探针并回写最近探测结果。
     */
    @Override
    @Transactional
    public HealthCheckResult test(Long tenantId, Long profileId) {
        FileStorageProfileEntity entity = requireProfile(tenantId, profileId);
        StorageProfileConfig config = StorageProfileConfig.fromJson(entity.getConfigJson(), objectMapper);
        HealthCheckResult result = storageAdapterRouter.testConnection(entity.getStorageBackend(), config);
        entity.setLastTestStatus(result.getStatus());
        entity.setLastTestAt(new Date());
        profileMapper.updateById(entity);
        return result;
    }

    @Override
    public List<FileBackendSchemaVO> listBackendSchemas() {
        List<FileBackendSchemaVO> list = new ArrayList<FileBackendSchemaVO>();
        list.add(schema(StorageBackend.LOCAL, "本地磁盘", "storageRoot", "开发/小规模私有化"));
        list.add(schema(StorageBackend.MINIO, "MinIO", "endpoint,accessKey,secretKey,bucket", "S3 兼容自建"));
        list.add(schema(StorageBackend.ALIYUN_OSS, "阿里云 OSS", "endpoint,accessKeyId,accessKeySecret,bucket 或 httpUrl", "SDK 或 HTTP 网关"));
        list.add(schema(StorageBackend.TENCENT_COS, "腾讯 COS", "endpoint,secretId,secretKey,bucket 或 httpUrl", "SDK 或 HTTP 网关"));
        list.add(schema(StorageBackend.HUAWEI_OBS, "华为 OBS", "endpoint,accessKey,secretKey,bucket 或 httpUrl", "SDK 或 HTTP 网关"));
        list.add(schema(StorageBackend.QINIU_KODO, "七牛云", "httpUrl,accessKey,secretKey,bucket", "建议 HTTP 网关"));
        list.add(schema(StorageBackend.BAIDU_BOS, "百度云 BOS", "endpoint,accessKey,secretKey,bucket 或 httpUrl", "SDK 或 HTTP 网关"));
        list.add(schema(StorageBackend.S3_COMPAT, "S3 兼容", "endpoint,accessKey,secretKey,bucket", "兼容 S3 API 的存储"));
        return list;
    }

    private FileBackendSchemaVO schema(String backend, String label, String fields, String hint) {
        FileBackendSchemaVO vo = new FileBackendSchemaVO();
        vo.setBackend(backend);
        vo.setLabel(label);
        vo.setRequiredFields(Arrays.asList(fields.split(",")));
        vo.setHint(hint);
        return vo;
    }

    private void clearDefault(Long tenantId, Long keepId) {
        FileStorageProfileEntity patch = new FileStorageProfileEntity();
        patch.setIsDefault(0);
        profileMapper.update(patch, new LambdaUpdateWrapper<FileStorageProfileEntity>()
                .eq(FileStorageProfileEntity::getTenantId, tenantId)
                .ne(FileStorageProfileEntity::getId, keepId));
    }

    private void mergeSecrets(FileStorageProfileEntity existing, StorageProfileConfig incoming) {
        if (existing == null || !StringUtils.hasText(existing.getConfigJson())) {
            return;
        }
        StorageProfileConfig old = StorageProfileConfig.fromJson(existing.getConfigJson(), objectMapper);
        if (!StringUtils.hasText(incoming.getSecretKey()) && StringUtils.hasText(old.getSecretKey())) {
            incoming.setSecretKey(old.getSecretKey());
        }
        if (!StringUtils.hasText(incoming.getAccessKeySecret()) && StringUtils.hasText(old.getAccessKeySecret())) {
            incoming.setAccessKeySecret(old.getAccessKeySecret());
        }
        if (!StringUtils.hasText(incoming.getAccessKey()) && StringUtils.hasText(old.getAccessKey())) {
            incoming.setAccessKey(old.getAccessKey());
        }
        if (!StringUtils.hasText(incoming.getAccessKeyId()) && StringUtils.hasText(old.getAccessKeyId())) {
            incoming.setAccessKeyId(old.getAccessKeyId());
        }
        if (!StringUtils.hasText(incoming.getSecretId()) && StringUtils.hasText(old.getSecretId())) {
            incoming.setSecretId(old.getSecretId());
        }
    }

    private StorageProfileConfig mapConfig(Map<String, Object> map) {
        if (map == null || map.isEmpty()) {
            return new StorageProfileConfig();
        }
        return objectMapper.convertValue(map, StorageProfileConfig.class);
    }

    private FileStorageProfileEntity requireProfile(Long tenantId, Long profileId) {
        FileStorageProfileEntity entity = profileMapper.selectOne(new LambdaQueryWrapper<FileStorageProfileEntity>()
                .eq(FileStorageProfileEntity::getTenantId, tenantId)
                .eq(FileStorageProfileEntity::getId, profileId)
                .last("limit 1"));
        if (entity == null) {
            throw new BusinessException(404, "storage profile not found");
        }
        return entity;
    }

    private FileStorageProfileVO toVo(FileStorageProfileEntity entity, boolean includeSecrets) {
        FileStorageProfileVO vo = new FileStorageProfileVO();
        vo.setId(entity.getId());
        vo.setTenantId(entity.getTenantId());
        vo.setProfileCode(entity.getProfileCode());
        vo.setProfileName(entity.getProfileName());
        vo.setStorageBackend(entity.getStorageBackend());
        StorageProfileConfig config = StorageProfileConfig.fromJson(entity.getConfigJson(), objectMapper);
        vo.setConfig(maskConfig(config, includeSecrets));
        vo.setEnabled(entity.getEnabled() != null && entity.getEnabled() == 1);
        vo.setDefaultProfile(entity.getIsDefault() != null && entity.getIsDefault() == 1);
        vo.setLastTestStatus(entity.getLastTestStatus());
        vo.setLastTestAt(entity.getLastTestAt());
        vo.setConfigured(config.isHttpMode() || config.isSdkReady()
                || (StorageBackend.LOCAL.equals(entity.getStorageBackend())
                && StringUtils.hasText(config.getStorageRoot())));
        return vo;
    }

    private Map<String, Object> maskConfig(StorageProfileConfig config, boolean includeSecrets) {
        Map<String, Object> map = objectMapper.convertValue(config,
                objectMapper.getTypeFactory().constructMapType(HashMap.class, String.class, Object.class));
        if (!includeSecrets) {
            for (String key : SECRET_KEYS) {
                if (map.containsKey(key) && map.get(key) != null && StringUtils.hasText(String.valueOf(map.get(key)))) {
                    map.put(key, "******");
                }
            }
        }
        return map;
    }
}
