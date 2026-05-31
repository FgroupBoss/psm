package com.fgroupboss.ai.psm.risk.majorhazard.service.impl;

import com.fgroupboss.ai.psm.risk.majorhazard.client.AlarmServiceClient;
import com.fgroupboss.ai.psm.risk.majorhazard.model.vo.HazardAlarmSummaryVO;
import com.fgroupboss.ai.psm.risk.majorhazard.service.HazardAlarmQueryService;
import com.fgroupboss.ai.psm.risk.majorhazard.client.dto.AlarmEventSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 瀹炵幇鏂瑰紡锛氭壙杞藉嵄闄╂簮鎶ヨ鏌ヨ涓氬姟瀹炵幇锛屽熀浜?Mapper銆佽繙绋嬪鎴风鎴栨敮鎾戠粍浠跺畬鎴愭牎楠屻€佺姸鎬佹祦杞拰缁撴灉缁勮銆?
 */
@Service
@RequiredArgsConstructor
public class HazardAlarmQueryServiceImpl implements HazardAlarmQueryService {

    private final AlarmServiceClient alarmServiceClient;

    /**
     * 瀹炵幇鏂瑰紡锛氭寜閲嶅ぇ鍗遍櫓婧愭煡璇㈠叧鑱旀姤璀︼紝鍏堝畬鎴愬繀瑕佺殑鍙傛暟銆佺鎴锋垨鐘舵€佹牎楠岋紝鍐嶅鎵樻寔涔呭寲缁勪欢鎴栬繙绋嬪鎴风澶勭悊骞剁粍瑁呰繑鍥炵粨鏋溿€?     */
    @Override
    public List<HazardAlarmSummaryVO> listByHazard(Long tenantId, Long hazardId) {
        List<AlarmEventSummary> events = alarmServiceClient.listByHazard(tenantId, hazardId);
        List<HazardAlarmSummaryVO> result = new ArrayList<HazardAlarmSummaryVO>();
        for (AlarmEventSummary event : events) {
            HazardAlarmSummaryVO vo = new HazardAlarmSummaryVO();
            vo.setId(event.getId());
            vo.setAlarmNo(event.getAlarmNo());
            vo.setTitle(event.getTitle());
            vo.setAlarmLevel(event.getAlarmLevel());
            vo.setStatus(event.getStatus());
            vo.setOccurrenceCount(event.getOccurrenceCount());
            vo.setLastOccurredAt(formatTime(event.getLastOccurredAt()));
            result.add(vo);
        }
        return result;
    }

    private String formatTime(Date value) {
        if (value == null) {
            return null;
        }
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(value);
    }
}

