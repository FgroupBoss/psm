package com.fgroupboss.ai.psm.file.service;

import com.fgroupboss.ai.psm.file.model.vo.FileHealthVO;
import com.fgroupboss.ai.psm.file.model.vo.FileObjectVO;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    FileHealthVO health();

    FileObjectVO upload(Long tenantId, MultipartFile file, String bizType, Long bizId, String operator);

    FileObjectVO get(Long tenantId, Long id);

    Resource loadAsResource(Long tenantId, Long id);
}
