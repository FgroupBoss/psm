package com.fgroupboss.ai.psm.risk.majorhazard.service;

import com.fgroupboss.ai.psm.risk.majorhazard.model.dto.HazardAttachmentRequest;
import com.fgroupboss.ai.psm.risk.majorhazard.model.vo.HazardAttachmentVO;

import java.util.List;

public interface MajorHazardAttachmentService {

    List<HazardAttachmentVO> listAttachments(Long tenantId, Long hazardId);

    HazardAttachmentVO createAttachment(Long tenantId, Long hazardId, HazardAttachmentRequest request, String operator);

    void deleteAttachment(Long tenantId, Long hazardId, Long attachmentId, String operator);
}
