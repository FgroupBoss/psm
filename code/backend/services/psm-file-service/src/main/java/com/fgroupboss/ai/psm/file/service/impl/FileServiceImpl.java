package com.fgroupboss.ai.psm.file.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.file.mapper.FileObjectMapper;
import com.fgroupboss.ai.psm.file.model.entity.FileObjectEntity;
import com.fgroupboss.ai.psm.file.model.vo.FileHealthVO;
import com.fgroupboss.ai.psm.file.model.vo.FileObjectVO;
import com.fgroupboss.ai.psm.file.service.FileService;
import com.fgroupboss.ai.psm.file.support.LocalFileStorageSupport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

/**
 * 文件元数据与本地存储编排。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final FileObjectMapper fileObjectMapper;
    private final LocalFileStorageSupport storageSupport;

    @Override
    public FileHealthVO health() {
        FileHealthVO vo = new FileHealthVO();
        vo.setService("psm-file-service");
        vo.setModule("file");
        vo.setVersion("1.0.0");
        vo.setFileCount(fileObjectMapper.selectCount(new LambdaQueryWrapper<FileObjectEntity>()));
        return vo;
    }

    @Override
    public FileObjectVO upload(Long tenantId, MultipartFile file, String bizType, Long bizId, String operator) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(400, "file is required");
        }
        try {
            LocalFileStorageSupport.StoredFile stored = storageSupport.store(file.getInputStream(),
                    file.getOriginalFilename());
            FileObjectEntity entity = new FileObjectEntity();
            entity.setTenantId(tenantId);
            entity.setFileName(StringUtils.hasText(file.getOriginalFilename()) ? file.getOriginalFilename() : "upload.bin");
            entity.setContentType(StringUtils.hasText(file.getContentType()) ? file.getContentType() : "application/octet-stream");
            entity.setSizeBytes(stored.getSizeBytes());
            entity.setSha256(stored.getSha256());
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
            log.info("file uploaded id={} tenantId={} size={}", entity.getId(), tenantId, stored.getSizeBytes());
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
        return new FileSystemResource(storageSupport.resolve(entity.getStoragePath()).toFile());
    }

    private FileObjectEntity requireEntity(Long tenantId, Long id) {
        FileObjectEntity entity = fileObjectMapper.selectOne(new LambdaQueryWrapper<FileObjectEntity>()
                .eq(FileObjectEntity::getTenantId, tenantId)
                .eq(FileObjectEntity::getId, id)
                .last("limit 1"));
        if (entity == null) {
            throw new BusinessException(404, "file not found");
        }
        return entity;
    }

    private FileObjectVO toVo(FileObjectEntity entity) {
        FileObjectVO vo = new FileObjectVO();
        vo.setId(entity.getId());
        vo.setTenantId(entity.getTenantId());
        vo.setFileName(entity.getFileName());
        vo.setContentType(entity.getContentType());
        vo.setSizeBytes(entity.getSizeBytes());
        vo.setSha256(entity.getSha256());
        vo.setBizType(entity.getBizType());
        vo.setBizId(entity.getBizId());
        vo.setStatus(entity.getStatus());
        vo.setCreatedAt(entity.getCreatedAt());
        vo.setDownloadUrl("/api/files/" + entity.getId() + "/download?tenantId=" + entity.getTenantId());
        return vo;
    }
}
