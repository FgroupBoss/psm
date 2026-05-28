package com.fgroupboss.ai.psm.location.service;

import com.fgroupboss.ai.psm.location.model.vo.AreaHeadcountVO;
import com.fgroupboss.ai.psm.location.model.vo.LocRealtimeVO;
import com.fgroupboss.ai.psm.location.model.vo.LocTrackPointVO;

import java.time.LocalDateTime;
import java.util.List;

public interface LocLocationService {

    List<LocRealtimeVO> listRealtime(Long tenantId, Long areaId, Long personId, String tagNo, String onlineStatus);

    List<LocTrackPointVO> listTracks(Long tenantId, String tagNo, Long personId, Long areaId,
                                     LocalDateTime fromTime, LocalDateTime toTime, int limit);

    AreaHeadcountVO headcount(Long tenantId, Long areaId);
}
