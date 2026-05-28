package com.fgroupboss.ai.psm.location.service.impl;

import com.fgroupboss.ai.psm.location.mapper.VehicleAccessRecordMapper;
import com.fgroupboss.ai.psm.location.model.dto.VehicleRecordIngestRequest;
import com.fgroupboss.ai.psm.location.model.entity.VehicleAccessRecordEntity;
import com.fgroupboss.ai.psm.location.model.vo.VehicleAccessRecordVO;
import com.fgroupboss.ai.psm.location.service.VehicleAccessService;
import com.fgroupboss.ai.psm.location.support.LocationSupport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class VehicleAccessServiceImpl implements VehicleAccessService {

    private final VehicleAccessRecordMapper vehicleMapper;

    @Override
    @Transactional
    public VehicleAccessRecordVO ingest(VehicleRecordIngestRequest request) {
        LocationSupport.requireTenantId(request.getTenantId());
        VehicleAccessRecordEntity entity = new VehicleAccessRecordEntity();
        entity.setTenantId(request.getTenantId());
        entity.setPlateNo(request.getPlateNo().trim());
        entity.setVehicleType(request.getVehicleType());
        entity.setGateCode(request.getGateCode());
        entity.setDirection(request.getDirection().trim());
        entity.setAccessTime(request.getAccessTime() == null ? LocalDateTime.now() : request.getAccessTime());
        entity.setDriverName(request.getDriverName());
        vehicleMapper.insert(entity);
        return toVO(entity);
    }

    private VehicleAccessRecordVO toVO(VehicleAccessRecordEntity entity) {
        VehicleAccessRecordVO vo = new VehicleAccessRecordVO();
        vo.setId(entity.getId());
        vo.setTenantId(entity.getTenantId());
        vo.setPlateNo(entity.getPlateNo());
        vo.setVehicleType(entity.getVehicleType());
        vo.setGateCode(entity.getGateCode());
        vo.setDirection(entity.getDirection());
        vo.setAccessTime(entity.getAccessTime());
        vo.setDriverName(entity.getDriverName());
        return vo;
    }
}
