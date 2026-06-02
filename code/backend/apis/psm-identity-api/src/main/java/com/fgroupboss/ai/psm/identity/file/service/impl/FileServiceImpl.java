package com.fgroupboss.ai.psm.identity.file.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.identity.file.config.FileProperties;
import com.fgroupboss.ai.psm.identity.file.config.StorageBackend;
import com.fgroupboss.ai.psm.identity.file.mapper.FileObjectMapper;
import com.fgroupboss.ai.psm.identity.file.model.entity.FileObjectEntity;
import com.fgroupboss.ai.psm.identity.file.model.vo.FileHealthVO;
import com.fgroupboss.ai.psm.identity.file.model.vo.FileObjectVO;
import com.fgroupboss.ai.psm.identity.file.model.vo.FilePresignVO;
import com.fgroupboss.ai.psm.identity.file.service.FileService;
import com.fgroupboss.ai.psm.identity.file.storage.PresignedUrl;
import com.fgroupboss.ai.psm.identity.file.storage.StoreContext;
import com.fgroupboss.ai.psm.identity.file.storage.StorageAdapterRouter;
import com.fgroupboss.ai.psm.identity.file.storage.StoredObject;
import com.fgroupboss.ai.psm.identity.file.storage.StoredObjectRef;
import com.fgroupboss.ai.psm.identity.file.storage.StorageProfileConfig;
import com.fgroupboss.ai.psm.identity.file.support.FilePolicySupport;
import com.fgroupboss.ai.psm.identity.file.support.FileStorageProfileResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

/**
 * 文件对象服务实现。
 *
 * <p>按租户存储配置档选择 {@link com.fgroupboss.ai.psm.identity.file.storage.StorageAdapterRouter}，
 * 元数据落库 {@code file_object}；云存储下载可走预签名重定向。</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final FileObjectMapper fileObjectMapper;
    private final FilePolicySupport filePolicySupport;
    private final FileStorageProfileResolver profileResolver;
    private final StorageAdapterRouter storageAdapterRouter;
    private final FileProperties fileProperties;

    @Override
    public FileHealthVO health() {
        FileHealthVO vo = new FileHealthVO();
        vo.setService("psm-file-service");
        vo.setModule("file");
        vo.setVersion("1.0.0");
        vo.setFileCount(fileObjectMapper.selectCount(new LambdaQueryWrapper<FileObjectEntity>()));
        return vo;
    }

    /**
     * 实现方式：校验上传策略并解析存储配置档，经适配器写入对象存储后持久化元数据。
     */
    @Override
    public FileObjectVO upload(Long tenantId, MultipartFile file, String bizType, Long bizId, String operator,
                               String storageProfileCode) {
        filePolicySupport.validateUpload(file);
        FileStorageProfileResolver.ResolvedProfile profile = profileResolver.resolve(tenantId, storageProfileCode);
        validateProfileReady(profile);

        StoreContext ctx = new StoreContext();
        ctx.setTenantId(tenantId);
        ctx.setStorageBackend(profile.getStorageBackend());
        ctx.setStorageProfileId(profile.getProfileId());
        ctx.setBucket(profile.resolveBucket());
        ctx.setBizType(bizType);
        ctx.setContentType(file.getContentType());
        ctx.setContentLength(file.getSize());

        try {
            InputStream inputStream = file.getInputStream();
            StoredObject stored = storageAdapterRouter.store(
                    profile.getStorageBackend(),
                    ctx,
                    profile.getConfig(),
                    inputStream,
                    file.getOriginalFilename());
            if (stored.getSizeBytes() <= 0) {
                stored.setSizeBytes(file.getSize());
            }
            FileObjectEntity entity = new FileObjectEntity();
            entity.setTenantId(tenantId);
            entity.setFileName(StringUtils.hasText(file.getOriginalFilename()) ? file.getOriginalFilename() : "upload.bin");
            entity.setContentType(StringUtils.hasText(file.getContentType()) ? file.getContentType() : "application/octet-stream");
            entity.setSizeBytes(stored.getSizeBytes());
            entity.setSha256(stored.getSha256());
            entity.setStorageBackend(stored.getStorageBackend());
            entity.setStorageProfileId(profile.getProfileId());
            entity.setBucket(stored.getBucket());
            entity.setObjectKey(stored.getObjectKey());
            entity.setRegion(stored.getRegion());
            entity.setStoragePath(stored.getStoragePath());
            entity.setBizType(bizType);
            entity.setBizId(bizId);
            entity.setStatus("ACTIVE");
            entity.setCreatedBy(operator);
            Date now = new Date();
            entity.setCreatedAt(now);
            entity.setUpdatedAt(now);
            entity.setDeleted(0);
            fileObjectMapper.insert(entity);
            log.info("file uploaded id={} tenantId={} backend={} size={}",
                    entity.getId(), tenantId, stored.getStorageBackend(), stored.getSizeBytes());
            return toVo(entity);
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("file upload failed tenantId={}", tenantId, ex);
            throw new BusinessException(500, "file upload failed");
        }
    }

    @Override
    public FileObjectVO get(Long tenantId, Long id) {
        return toVo(requireEntity(tenantId, id));
    }

    @Override
    public Resource loadAsResource(Long tenantId, Long id) {
        FileObjectEntity entity = requireEntity(tenantId, id);
        if (shouldRedirectToPresign(tenantId, id)) {
            throw new BusinessException(400, "use presign download for object storage");
        }
        StoredObjectRef ref = toRef(entity);
        try {
            if (StorageBackend.LOCAL.equals(resolveBackend(entity))) {
                Path base = Paths.get(StringUtils.hasText(ref.getProfileConfig().getStorageRoot())
                        ? ref.getProfileConfig().getStorageRoot() : fileProperties.getStorageRoot())
                        .toAbsolutePath().normalize();
                Path path = base.resolve(entity.getStoragePath()).normalize();
                if (!path.startsWith(base) || !Files.exists(path)) {
                    throw new BusinessException(404, "file not found");
                }
                return new FileSystemResource(path.toFile());
            }
            InputStream stream = storageAdapterRouter.open(ref);
            return new InputStreamResource(stream);
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BusinessException(404, "file not found");
        }
    }

    /**
     * 实现方式：加载文件元数据与配置，委托路由器生成限时 GET 预签名 URL。
     */
    @Override
    public FilePresignVO presignDownload(Long tenantId, Long id) {
        FileObjectEntity entity = requireEntity(tenantId, id);
        StoredObjectRef ref = toRef(entity);
        PresignedUrl presigned = storageAdapterRouter.presignGet(ref, fileProperties.getPresignTtlSeconds());
        FilePresignVO vo = new FilePresignVO();
        vo.setFileId(entity.getId());
        vo.setUrl(presigned.getUrl());
        vo.setExpiresInSeconds(presigned.getExpiresInSeconds());
        vo.setDownloadMode("PRESIGNED");
        return vo;
    }

    /**
     * 实现方式：结合全局 downloadMode 与后端类型判断是否对下载/预览返回 302。
     */
    @Override
    public boolean shouldRedirectToPresign(Long tenantId, Long id) {
        FileObjectEntity entity = requireEntity(tenantId, id);
        if (StorageBackend.LOCAL.equals(resolveBackend(entity))) {
            return false;
        }
        if ("PROXY".equalsIgnoreCase(fileProperties.getDownloadMode())) {
            return false;
        }
        if ("PRESIGNED".equalsIgnoreCase(fileProperties.getDownloadMode())) {
            return true;
        }
        StorageProfileConfig config = profileResolver.loadConfigById(entity.getStorageProfileId());
        return config.isHttpMode() || StorageBackend.usesS3Api(entity.getStorageBackend());
    }

    private void validateProfileReady(FileStorageProfileResolver.ResolvedProfile profile) {
        StorageProfileConfig config = profile.getConfig();
        if (config.isHttpMode() || config.isSdkReady()) {
            return;
        }
        if (StorageBackend.LOCAL.equals(profile.getStorageBackend())) {
            return;
        }
        throw new BusinessException(400, "storage profile not configured, complete SDK or HTTP gateway settings");
    }

    private FileObjectEntity requireEntity(Long tenantId, Long id) {
        if (tenantId == null || id == null) {
            throw new BusinessException(400, "tenantId and id are required");
        }
        FileObjectEntity entity = fileObjectMapper.selectOne(new LambdaQueryWrapper<FileObjectEntity>()
                .eq(FileObjectEntity::getTenantId, tenantId)
                .eq(FileObjectEntity::getId, id)
                .last("limit 1"));
        if (entity == null) {
            throw new BusinessException(404, "file not found");
        }
        return entity;
    }

    private StoredObjectRef toRef(FileObjectEntity entity) {
        StoredObjectRef ref = new StoredObjectRef();
        ref.setTenantId(entity.getTenantId());
        ref.setStorageBackend(entity.getStorageBackend());
        ref.setBucket(entity.getBucket());
        ref.setObjectKey(entity.getObjectKey());
        ref.setStoragePath(entity.getStoragePath());
        ref.setFileName(entity.getFileName());
        ref.setContentType(entity.getContentType());
        ref.setProfileConfig(profileResolver.loadConfigById(entity.getStorageProfileId()));
        return ref;
    }

    private FileObjectVO toVo(FileObjectEntity entity) {
        FileObjectVO vo = new FileObjectVO();
        vo.setId(entity.getId());
        vo.setTenantId(entity.getTenantId());
        vo.setFileName(entity.getFileName());
        vo.setContentType(entity.getContentType());
        vo.setSizeBytes(entity.getSizeBytes());
        vo.setSha256(entity.getSha256());
        vo.setStorageBackend(entity.getStorageBackend());
        vo.setBizType(entity.getBizType());
        vo.setBizId(entity.getBizId());
        vo.setStatus(entity.getStatus());
        vo.setCreatedAt(entity.getCreatedAt());
        String base = "/api/files/" + entity.getId();
        vo.setDownloadUrl(base + "/download");
        vo.setPreviewUrl(base + "/preview");
        if (computePresignFlag(entity)) {
            vo.setPresignUrl(base + "/presign");
        }
        return vo;
    }

    private boolean computePresignFlag(FileObjectEntity entity) {
        if (StorageBackend.LOCAL.equals(resolveBackend(entity))) {
            return false;
        }
        if ("PROXY".equalsIgnoreCase(fileProperties.getDownloadMode())) {
            return false;
        }
        if ("PRESIGNED".equalsIgnoreCase(fileProperties.getDownloadMode())) {
            return true;
        }
        StorageProfileConfig config = profileResolver.loadConfigById(entity.getStorageProfileId());
        return config.isHttpMode() || StorageBackend.usesS3Api(resolveBackend(entity));
    }

    private String resolveBackend(FileObjectEntity entity) {
        if (StringUtils.hasText(entity.getStorageBackend())) {
            return entity.getStorageBackend();
        }
        return StorageBackend.LOCAL;
    }
}
