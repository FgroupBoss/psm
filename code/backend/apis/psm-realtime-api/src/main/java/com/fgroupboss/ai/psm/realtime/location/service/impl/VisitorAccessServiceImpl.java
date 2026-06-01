package com.fgroupboss.ai.psm.realtime.location.service.impl;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.realtime.location.mapper.VisitorAccessRecordMapper;
import com.fgroupboss.ai.psm.realtime.location.model.dto.VisitorRecordIngestRequest;
import com.fgroupboss.ai.psm.realtime.location.model.entity.VisitorAccessRecordEntity;
import com.fgroupboss.ai.psm.realtime.location.model.vo.VisitorAccessRecordVO;
import com.fgroupboss.ai.psm.realtime.location.service.VisitorAccessService;
import com.fgroupboss.ai.psm.realtime.location.support.LocationSupport;
import com.fgroupboss.ai.psm.realtime.location.support.LocationSupport.PageSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VisitorAccessServiceImpl implements VisitorAccessService {

    private final VisitorAccessRecordMapper visitorMapper;

    @Override
    @Transactional
    public VisitorAccessRecordVO ingest(VisitorRecordIngestRequest request) {
        LocationSupport.requireTenantId(request.getTenantId());
        VisitorAccessRecordEntity entity = new VisitorAccessRecordEntity();
        entity.setTenantId(request.getTenantId());
        entity.setVisitorName(request.getVisitorName().trim());
        entity.setIdCardNo(request.getIdCardNo());
        entity.setCompanyName(request.getCompanyName());
        entity.setHostPersonId(request.getHostPersonId());
        entity.setGateCode(request.getGateCode());
        entity.setTagNo(request.getTagNo());
        entity.setVisitPurpose(request.getVisitPurpose());
        entity.setDirection(request.getDirection().trim());
        entity.setAccessTime(request.getAccessTime() == null ? LocalDateTime.now() : request.getAccessTime());
        entity.setAccessResult(request.getAccessResult());
        visitorMapper.insert(entity);
        return toVO(entity);
    }

    @Override
    public PageResult<VisitorAccessRecordVO> page(Long tenantId, String visitorName, String gateCode,
                                                  LocalDateTime fromTime, LocalDateTime toTime,
                                                  int pageNo, int pageSize) {
        LocationSupport.requireTenantId(tenantId);
        PageSpec page = LocationSupport.normalizePage(pageNo, pageSize);
        String normalizedVisitorName = LocationSupport.normalizeText(visitorName);
        String normalizedGateCode = LocationSupport.normalizeText(gateCode);
        long total = visitorMapper.countByTenant(tenantId, normalizedVisitorName, normalizedGateCode, fromTime, toTime);
        List<VisitorAccessRecordEntity> entities = total == 0
                ? new ArrayList<VisitorAccessRecordEntity>()
                : visitorMapper.listByTenant(tenantId, normalizedVisitorName, normalizedGateCode, fromTime, toTime,
                page.getOffset(), page.getPageSize());
        List<VisitorAccessRecordVO> records = new ArrayList<VisitorAccessRecordVO>();
        for (VisitorAccessRecordEntity entity : entities) {
            records.add(toVO(entity));
        }
        return new PageResult<VisitorAccessRecordVO>(total, page.getPageNo(), page.getPageSize(), records);
    }

    private VisitorAccessRecordVO toVO(VisitorAccessRecordEntity entity) {
        VisitorAccessRecordVO vo = new VisitorAccessRecordVO();
        vo.setId(entity.getId());
        vo.setTenantId(entity.getTenantId());
        vo.setVisitorName(entity.getVisitorName());
        vo.setIdCardNo(entity.getIdCardNo());
        vo.setCompanyName(entity.getCompanyName());
        vo.setHostPersonId(entity.getHostPersonId());
        vo.setGateCode(entity.getGateCode());
        vo.setTagNo(entity.getTagNo());
        vo.setVisitPurpose(entity.getVisitPurpose());
        vo.setDirection(entity.getDirection());
        vo.setAccessTime(entity.getAccessTime());
        vo.setAccessResult(entity.getAccessResult());
        return vo;
    }
}
