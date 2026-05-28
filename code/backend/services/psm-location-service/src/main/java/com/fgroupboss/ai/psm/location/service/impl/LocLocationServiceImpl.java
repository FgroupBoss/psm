package com.fgroupboss.ai.psm.location.service.impl;

import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.location.config.LocationConstants;
import com.fgroupboss.ai.psm.location.mapper.LocRealtimeMapper;
import com.fgroupboss.ai.psm.location.mapper.LocTrackPointMapper;
import com.fgroupboss.ai.psm.location.model.entity.LocRealtimeEntity;
import com.fgroupboss.ai.psm.location.model.entity.LocTrackPointEntity;
import com.fgroupboss.ai.psm.location.model.vo.AreaHeadcountVO;
import com.fgroupboss.ai.psm.location.model.vo.LocRealtimeVO;
import com.fgroupboss.ai.psm.location.model.vo.LocTrackPointVO;
import com.fgroupboss.ai.psm.location.service.LocLocationService;
import com.fgroupboss.ai.psm.location.support.LocationSupport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LocLocationServiceImpl implements LocLocationService {

    private final LocRealtimeMapper realtimeMapper;
    private final LocTrackPointMapper trackPointMapper;

    @Override
    public List<LocRealtimeVO> listRealtime(Long tenantId, Long areaId, Long personId, String tagNo, String onlineStatus) {
        LocationSupport.requireTenantId(tenantId);
        List<LocRealtimeEntity> entities = realtimeMapper.listByQuery(tenantId, areaId, personId,
                LocationSupport.normalizeText(tagNo), LocationSupport.normalizeText(onlineStatus));
        List<LocRealtimeVO> result = new ArrayList<LocRealtimeVO>();
        for (LocRealtimeEntity entity : entities) {
            result.add(toRealtimeVO(entity));
        }
        return result;
    }

    @Override
    public List<LocTrackPointVO> listTracks(Long tenantId, String tagNo, Long personId, Long areaId,
                                            LocalDateTime fromTime, LocalDateTime toTime, int limit) {
        LocationSupport.requireTenantId(tenantId);
        int normalizedLimit = Math.min(Math.max(limit, 1), 5000);
        List<LocTrackPointEntity> entities = trackPointMapper.listTracks(tenantId,
                LocationSupport.normalizeText(tagNo), personId, areaId, fromTime, toTime, normalizedLimit);
        List<LocTrackPointVO> result = new ArrayList<LocTrackPointVO>();
        for (LocTrackPointEntity entity : entities) {
            result.add(toTrackVO(entity));
        }
        return result;
    }

    @Override
    public AreaHeadcountVO headcount(Long tenantId, Long areaId) {
        LocationSupport.requireTenantId(tenantId);
        if (areaId == null || areaId <= 0) {
            throw new BusinessException(400, "areaId is required");
        }
        int total = realtimeMapper.countHeadcount(tenantId, areaId, null);
        int online = realtimeMapper.countHeadcount(tenantId, areaId, LocationConstants.ONLINE);
        AreaHeadcountVO vo = new AreaHeadcountVO();
        vo.setTenantId(tenantId);
        vo.setAreaId(areaId);
        vo.setHeadcount(total);
        vo.setOnlineCount(online);
        return vo;
    }

    private LocRealtimeVO toRealtimeVO(LocRealtimeEntity entity) {
        LocRealtimeVO vo = new LocRealtimeVO();
        vo.setId(entity.getId());
        vo.setTenantId(entity.getTenantId());
        vo.setTagNo(entity.getTagNo());
        vo.setPersonId(entity.getPersonId());
        vo.setAreaId(entity.getAreaId());
        vo.setLatitude(entity.getLatitude());
        vo.setLongitude(entity.getLongitude());
        vo.setOnlineStatus(entity.getOnlineStatus());
        vo.setLastSeenAt(entity.getLastSeenAt());
        return vo;
    }

    private LocTrackPointVO toTrackVO(LocTrackPointEntity entity) {
        LocTrackPointVO vo = new LocTrackPointVO();
        vo.setId(entity.getId());
        vo.setTagNo(entity.getTagNo());
        vo.setPersonId(entity.getPersonId());
        vo.setAreaId(entity.getAreaId());
        vo.setLatitude(entity.getLatitude());
        vo.setLongitude(entity.getLongitude());
        vo.setRecordedAt(entity.getRecordedAt());
        return vo;
    }
}
