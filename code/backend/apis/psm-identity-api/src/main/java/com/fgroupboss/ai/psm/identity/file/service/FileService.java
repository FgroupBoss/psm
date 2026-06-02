package com.fgroupboss.ai.psm.identity.file.service;

import com.fgroupboss.ai.psm.identity.file.model.vo.FileHealthVO;
import com.fgroupboss.ai.psm.identity.file.model.vo.FileObjectVO;
import com.fgroupboss.ai.psm.identity.file.model.vo.FilePresignVO;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    FileHealthVO health();

    FileObjectVO upload(Long tenantId, MultipartFile file, String bizType, Long bizId, String operator,
                        String storageProfileCode);

    FileObjectVO get(Long tenantId, Long id);

    Resource loadAsResource(Long tenantId, Long id);

    FilePresignVO presignDownload(Long tenantId, Long id);

    boolean shouldRedirectToPresign(Long tenantId, Long id);
}
