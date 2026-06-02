package com.fgroupboss.ai.psm.identity.file.service;

import com.fgroupboss.ai.psm.identity.file.model.dto.FileStorageProfileRequest;
import com.fgroupboss.ai.psm.identity.file.model.vo.FileBackendSchemaVO;
import com.fgroupboss.ai.psm.identity.file.model.vo.FileStorageProfileVO;
import com.fgroupboss.ai.psm.identity.file.storage.HealthCheckResult;

import java.util.List;

/**
 * 租户存储配置档管理：列表、默认档、保存与连通性探针。
 */
public interface FileStorageProfileService {

    List<FileStorageProfileVO> list(Long tenantId);

    FileStorageProfileVO getDefault(Long tenantId);

    FileStorageProfileVO save(FileStorageProfileRequest request);

    HealthCheckResult test(Long tenantId, Long profileId);

    List<FileBackendSchemaVO> listBackendSchemas();
}
