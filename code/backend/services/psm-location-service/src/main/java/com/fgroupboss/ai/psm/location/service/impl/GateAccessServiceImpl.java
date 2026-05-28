package com.fgroupboss.ai.psm.location.service.impl;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.location.mapper.GateAccessRecordMapper;
import com.fgroupboss.ai.psm.location.model.dto.GateRecordIngestRequest;
import com.fgroupboss.ai.psm.location.model.entity.GateAccessRecordEntity;
import com.fgroupboss.ai.psm.location.model.vo.GateAccessRecordVO;
import com.fgroupboss.ai.psm.location.service.GateAccessService;
import com.fgroupboss.ai.psm.location.support.LocationSupport;
import com.fgroupboss.ai.psm.location.support.LocationSupport.PageSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GateAccessServiceImpl implements GateAccessService {

    private final GateAccessRecordMapper gateMapper;

    @Override
    @Transactional
    public GateAccessRecordVO ingest(GateRecordIngestRequest request) {
        LocationSupport.requireTenantId(request.getTenantId());
        GateAccessRecordEntity entity = new GateAccessRecordEntity();
        entity.setTenantId(request.getTenantId());
        entity.setGateCode(request.getGateCode().trim());
        entity.setCardNo(request.getCardNo());
        entity.setTagNo(request.getTagNo());
        entity.setPersonId(request.getPersonId());
        entity.setDirection(request.getDirection().trim());
        entity.setAccessTime(request.getAccessTime() == null ? LocalDateTime.now() : request.getAccessTime());
        entity.setAccessResult(request.getAccessResult());
        gateMapper.insert(entity);
        return toVO(entity);
    }

    @Override
    public PageResult<GateAccessRecordVO> page(Long tenantId, String gateCode, Long personId,
                                               LocalDateTime fromTime, LocalDateTime toTime,
                                               int pageNo, int pageSize) {
        LocationSupport.requireTenantId(tenantId);
        PageSpec page = LocationSupport.normalizePage(pageNo, pageSize);
        String normalizedGateCode = LocationSupport.normalizeText(gateCode);
        long total = gateMapper.countByTenant(tenantId, normalizedGateCode, personId, fromTime, toTime);
        List<GateAccessRecordEntity> entities = total == 0
                ? new ArrayList<GateAccessRecordEntity>()
                : gateMapper.listByTenant(tenantId, normalizedGateCode, personId, fromTime, toTime,
                page.getOffset(), page.getPageSize());
        List<GateAccessRecordVO> records = new ArrayList<GateAccessRecordVO>();
        for (GateAccessRecordEntity entity : entities) {
            records.add(toVO(entity));
        }
        return new PageResult<GateAccessRecordVO>(total, page.getPageNo(), page.getPageSize(), records);
    }

    private GateAccessRecordVO toVO(GateAccessRecordEntity entity) {
        GateAccessRecordVO vo = new GateAccessRecordVO();
        vo.setId(entity.getId());
        vo.setTenantId(entity.getTenantId());
        vo.setGateCode(entity.getGateCode());
        vo.setCardNo(entity.getCardNo());
        vo.setTagNo(entity.getTagNo());
        vo.setPersonId(entity.getPersonId());
        vo.setDirection(entity.getDirection());
        vo.setAccessTime(entity.getAccessTime());
        vo.setAccessResult(entity.getAccessResult());
        return vo;
    }
}
