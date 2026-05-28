package com.fgroupboss.ai.psm.location.service.impl;

import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.location.client.ContractorEligibilityClient;
import com.fgroupboss.ai.psm.location.client.dto.ContractorEligibilityReason;
import com.fgroupboss.ai.psm.location.client.dto.ContractorEligibilityRequest;
import com.fgroupboss.ai.psm.location.client.dto.ContractorEligibilityResult;
import com.fgroupboss.ai.psm.location.config.LocationConstants;
import com.fgroupboss.ai.psm.location.mapper.LocTagBindingMapper;
import com.fgroupboss.ai.psm.location.mapper.LocTagMapper;
import com.fgroupboss.ai.psm.location.model.dto.LocTagBindRequest;
import com.fgroupboss.ai.psm.location.model.dto.LocTagRequest;
import com.fgroupboss.ai.psm.location.model.entity.LocTagBindingEntity;
import com.fgroupboss.ai.psm.location.model.entity.LocTagEntity;
import com.fgroupboss.ai.psm.location.model.vo.LocTagBindingVO;
import com.fgroupboss.ai.psm.location.model.vo.LocTagVO;
import com.fgroupboss.ai.psm.location.service.LocTagService;
import com.fgroupboss.ai.psm.location.support.LocationSupport;
import com.fgroupboss.ai.psm.location.support.LocationSupport.PageSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LocTagServiceImpl implements LocTagService {

    private final LocTagMapper tagMapper;
    private final LocTagBindingMapper bindingMapper;
    private final ContractorEligibilityClient contractorEligibilityClient;

    @Override
    public PageResult<LocTagVO> page(Long tenantId, String keyword, String status, int pageNo, int pageSize) {
        LocationSupport.requireTenantId(tenantId);
        PageSpec page = LocationSupport.normalizePage(pageNo, pageSize);
        String normalizedKeyword = LocationSupport.normalizeText(keyword);
        String normalizedStatus = LocationSupport.normalizeText(status);
        long total = tagMapper.countByTenant(tenantId, normalizedKeyword, normalizedStatus);
        List<LocTagEntity> entities = total == 0
                ? new ArrayList<LocTagEntity>()
                : tagMapper.listByTenant(tenantId, normalizedKeyword, normalizedStatus, page.getOffset(), page.getPageSize());
        List<LocTagVO> records = new ArrayList<LocTagVO>();
        for (LocTagEntity entity : entities) {
            records.add(toTagVO(entity, bindingMapper.findActiveByTagNo(tenantId, entity.getTagNo())));
        }
        return new PageResult<LocTagVO>(total, page.getPageNo(), page.getPageSize(), records);
    }

    @Override
    public LocTagVO getByTagNo(Long tenantId, String tagNo) {
        LocTagEntity tag = requireTag(tenantId, tagNo);
        return toTagVO(tag, bindingMapper.findActiveByTagNo(tenantId, tag.getTagNo()));
    }

    @Override
    @Transactional
    public LocTagVO create(LocTagRequest request) {
        LocationSupport.requireTenantId(request.getTenantId());
        assertTagNoUnique(request.getTenantId(), request.getTagNo(), null);
        LocTagEntity entity = new LocTagEntity();
        entity.setTenantId(request.getTenantId());
        entity.setTagNo(request.getTagNo().trim());
        entity.setTagType(StringUtils.hasText(request.getTagType()) ? request.getTagType().trim() : "LOCATION");
        entity.setVendorCode(request.getVendorCode());
        entity.setStatus(StringUtils.hasText(request.getStatus()) ? request.getStatus().trim() : "ENABLED");
        entity.setRemark(request.getRemark());
        entity.setDeleted(0);
        tagMapper.insert(entity);
        return toTagVO(entity, null);
    }

    @Override
    @Transactional
    public LocTagBindingVO bind(String tagNo, LocTagBindRequest request) {
        LocTagEntity tag = requireTag(request.getTenantId(), tagNo);
        assertContractorEligible(request);
        LocTagBindingEntity active = bindingMapper.findActiveByTagNo(request.getTenantId(), tag.getTagNo());
        if (active != null) {
            throw new BusinessException(400, "tag already bound: " + tag.getTagNo());
        }
        LocTagBindingEntity binding = new LocTagBindingEntity();
        binding.setTenantId(request.getTenantId());
        binding.setTagNo(tag.getTagNo());
        binding.setPersonType(request.getPersonType().trim());
        binding.setPersonId(request.getPersonId());
        binding.setContractorId(request.getContractorId());
        binding.setBindAt(LocalDateTime.now());
        binding.setStatus(LocationConstants.BINDING_ACTIVE);
        binding.setDeleted(0);
        bindingMapper.insert(binding);
        return toBindingVO(binding);
    }

    @Override
    @Transactional
    public LocTagBindingVO unbind(String tagNo, Long tenantId) {
        requireTag(tenantId, tagNo);
        LocTagBindingEntity active = bindingMapper.findActiveByTagNo(tenantId, tagNo.trim());
        if (active == null) {
            throw new BusinessException(404, "active binding not found");
        }
        active.setStatus(LocationConstants.BINDING_UNBOUND);
        active.setUnbindAt(LocalDateTime.now());
        bindingMapper.updateById(active);
        return toBindingVO(active);
    }

    private void assertContractorEligible(LocTagBindRequest request) {
        if (!"CONTRACTOR".equalsIgnoreCase(request.getPersonType().trim())) {
            return;
        }
        if (request.getContractorId() == null) {
            throw new BusinessException(400, "contractorId is required for CONTRACTOR binding");
        }
        ContractorEligibilityRequest eligibilityRequest = new ContractorEligibilityRequest();
        eligibilityRequest.setTenantId(request.getTenantId());
        eligibilityRequest.setCompanyId(request.getContractorId());
        eligibilityRequest.setWorkerIds(Collections.singletonList(request.getPersonId()));
        eligibilityRequest.setCheckPoint("LOC_TAG_BIND");
        ContractorEligibilityResult result = contractorEligibilityClient.check(eligibilityRequest);
        if (result == null || !result.isPassed()) {
            throw new BusinessException(403, buildEligibilityMessage(result));
        }
    }

    private String buildEligibilityMessage(ContractorEligibilityResult result) {
        if (result == null || result.getReasons() == null || result.getReasons().isEmpty()) {
            return "contractor eligibility check failed";
        }
        StringBuilder builder = new StringBuilder();
        for (ContractorEligibilityReason reason : result.getReasons()) {
            if (builder.length() > 0) {
                builder.append("; ");
            }
            if (reason.getMessage() != null) {
                builder.append(reason.getMessage());
            } else if (reason.getCode() != null) {
                builder.append(reason.getCode());
            }
        }
        return builder.toString();
    }

    private void assertTagNoUnique(Long tenantId, String tagNo, Long excludeId) {
        LocTagEntity existing = tagMapper.findByTagNo(tenantId, tagNo.trim());
        if (existing != null && (excludeId == null || !excludeId.equals(existing.getId()))) {
            throw new BusinessException(400, "tagNo already exists: " + tagNo);
        }
    }

    private LocTagEntity requireTag(Long tenantId, String tagNo) {
        LocationSupport.requireTenantId(tenantId);
        if (!StringUtils.hasText(tagNo)) {
            throw new BusinessException(400, "tagNo is required");
        }
        LocTagEntity entity = tagMapper.findByTagNo(tenantId, tagNo.trim());
        if (entity == null || entity.getDeleted() != null && entity.getDeleted() == 1) {
            throw new BusinessException(404, "tag not found");
        }
        return entity;
    }

    private LocTagVO toTagVO(LocTagEntity entity, LocTagBindingEntity binding) {
        LocTagVO vo = new LocTagVO();
        vo.setId(entity.getId());
        vo.setTenantId(entity.getTenantId());
        vo.setTagNo(entity.getTagNo());
        vo.setTagType(entity.getTagType());
        vo.setVendorCode(entity.getVendorCode());
        vo.setStatus(entity.getStatus());
        vo.setRemark(entity.getRemark());
        vo.setActiveBinding(binding == null ? null : toBindingVO(binding));
        return vo;
    }

    private LocTagBindingVO toBindingVO(LocTagBindingEntity entity) {
        LocTagBindingVO vo = new LocTagBindingVO();
        vo.setId(entity.getId());
        vo.setTenantId(entity.getTenantId());
        vo.setTagNo(entity.getTagNo());
        vo.setPersonType(entity.getPersonType());
        vo.setPersonId(entity.getPersonId());
        vo.setContractorId(entity.getContractorId());
        vo.setBindAt(entity.getBindAt());
        vo.setUnbindAt(entity.getUnbindAt());
        vo.setStatus(entity.getStatus());
        return vo;
    }
}
