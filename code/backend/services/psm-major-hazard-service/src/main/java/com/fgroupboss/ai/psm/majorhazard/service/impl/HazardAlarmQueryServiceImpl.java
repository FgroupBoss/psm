package com.fgroupboss.ai.psm.majorhazard.service.impl;

import com.fgroupboss.ai.psm.majorhazard.client.AlarmServiceClient;
import com.fgroupboss.ai.psm.majorhazard.client.dto.AlarmEventSummary;
import com.fgroupboss.ai.psm.majorhazard.model.vo.HazardAlarmSummaryVO;
import com.fgroupboss.ai.psm.majorhazard.service.HazardAlarmQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 实现方式：承载危险源报警查询业务实现，基于 Mapper、远程客户端或支撑组件完成校验、状态流转和结果组装。
 */
@Service
@RequiredArgsConstructor
public class HazardAlarmQueryServiceImpl implements HazardAlarmQueryService {

    private final AlarmServiceClient alarmServiceClient;

    /**
     * 实现方式：按重大危险源查询关联报警，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
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
