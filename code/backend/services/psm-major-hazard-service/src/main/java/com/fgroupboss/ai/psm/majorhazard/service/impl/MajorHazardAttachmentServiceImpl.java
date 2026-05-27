package com.fgroupboss.ai.psm.majorhazard.service.impl;

import com.fgroupboss.ai.psm.common.AuditBizType;
import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.majorhazard.mapper.MajorHazardAttachmentMapper;
import com.fgroupboss.ai.psm.majorhazard.mapper.MajorHazardMapper;
import com.fgroupboss.ai.psm.majorhazard.model.dto.HazardAttachmentRequest;
import com.fgroupboss.ai.psm.majorhazard.model.entity.MajorHazardAttachmentEntity;
import com.fgroupboss.ai.psm.majorhazard.model.entity.MajorHazardEntity;
import com.fgroupboss.ai.psm.majorhazard.model.vo.HazardAttachmentVO;
import com.fgroupboss.ai.psm.majorhazard.service.MajorHazardAttachmentService;
import com.fgroupboss.ai.psm.majorhazard.support.MajorHazardAuditSupport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MajorHazardAttachmentServiceImpl implements MajorHazardAttachmentService {

    private final MajorHazardMapper hazardMapper;
    private final MajorHazardAttachmentMapper attachmentMapper;
    private final MajorHazardAuditSupport auditSupport;

    @Override
    public List<HazardAttachmentVO> listAttachments(Long tenantId, Long hazardId) {
        requireHazard(tenantId, hazardId);
        List<MajorHazardAttachmentEntity> entities = attachmentMapper.listByHazardId(tenantId, hazardId);
        List<HazardAttachmentVO> records = new ArrayList<HazardAttachmentVO>();
        for (MajorHazardAttachmentEntity entity : entities) {
            records.add(toVO(entity));
        }
        return records;
    }

    @Override
    @Transactional
    public HazardAttachmentVO createAttachment(Long tenantId, Long hazardId, HazardAttachmentRequest request,
                                               String operator) {
        requireHazard(tenantId, hazardId);
        if (request.getFileId() == null || request.getFileId() <= 0) {
            throw new BusinessException(400, "fileId is required");
        }
        MajorHazardAttachmentEntity entity = new MajorHazardAttachmentEntity();
        entity.setTenantId(tenantId);
        entity.setHazardId(hazardId);
        entity.setAttachmentType(request.getAttachmentType().trim());
        entity.setFileId(request.getFileId());
        entity.setFileName(normalizeText(request.getFileName()));
        entity.setDeleted(0);
        attachmentMapper.insert(entity);
        auditSupport.write(tenantId, hazardId, AuditBizType.MAJOR_HAZARD_ATTACHMENT.name(), "CREATE",
                "附件 " + entity.getAttachmentType() + " fileId=" + entity.getFileId(), operator);
        return toVO(entity);
    }

    @Override
    @Transactional
    public void deleteAttachment(Long tenantId, Long hazardId, Long attachmentId, String operator) {
        requireHazard(tenantId, hazardId);
        MajorHazardAttachmentEntity entity = attachmentMapper.selectById(attachmentId);
        if (entity == null || entity.getDeleted() != null && entity.getDeleted() == 1
                || !tenantId.equals(entity.getTenantId()) || !hazardId.equals(entity.getHazardId())) {
            throw new BusinessException(404, "hazard attachment not found");
        }
        entity.setDeleted(1);
        attachmentMapper.updateById(entity);
        auditSupport.write(tenantId, hazardId, AuditBizType.MAJOR_HAZARD_ATTACHMENT.name(), "DELETE",
                "删除附件 " + attachmentId, operator);
    }

    private MajorHazardEntity requireHazard(Long tenantId, Long hazardId) {
        if (tenantId == null || tenantId <= 0) {
            throw new BusinessException(400, "tenantId is required");
        }
        MajorHazardEntity entity = hazardMapper.selectById(hazardId);
        if (entity == null || entity.getDeleted() != null && entity.getDeleted() == 1
                || !tenantId.equals(entity.getTenantId())) {
            throw new BusinessException(404, "major hazard not found");
        }
        return entity;
    }

    private HazardAttachmentVO toVO(MajorHazardAttachmentEntity entity) {
        HazardAttachmentVO vo = new HazardAttachmentVO();
        vo.setId(entity.getId());
        vo.setTenantId(entity.getTenantId());
        vo.setHazardId(entity.getHazardId());
        vo.setAttachmentType(entity.getAttachmentType());
        vo.setFileId(entity.getFileId());
        vo.setFileName(entity.getFileName());
        return vo;
    }

    private String normalizeText(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
