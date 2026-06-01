package com.fgroupboss.ai.psm.realtime.alarm.support;

import com.fgroupboss.ai.psm.realtime.alarm.mapper.AlarmDedupRuleMapper;
import com.fgroupboss.ai.psm.realtime.alarm.mapper.AlarmEventMapper;
import com.fgroupboss.ai.psm.realtime.alarm.model.entity.AlarmDedupRuleEntity;
import com.fgroupboss.ai.psm.realtime.alarm.model.entity.AlarmEventEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;

/**
 * 报警去重：按 dedup_key 与时间窗合并同源报警。
 */
@Component
@RequiredArgsConstructor
public class AlarmDedupSupport {

    private static final int DEFAULT_WINDOW_SECONDS = 300;

    private final AlarmDedupRuleMapper dedupRuleMapper;
    private final AlarmEventMapper alarmEventMapper;

    public int resolveWindowSeconds(Long tenantId, String sourceType) {
        AlarmDedupRuleEntity rule = dedupRuleMapper.findBySourceType(tenantId, sourceType);
        if (rule == null || rule.getWindowSeconds() == null || rule.getWindowSeconds() <= 0) {
            return DEFAULT_WINDOW_SECONDS;
        }
        return rule.getWindowSeconds().intValue();
    }

    public AlarmEventEntity findMergeCandidate(Long tenantId, String dedupKey, Date occurredAt, int windowSeconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(occurredAt);
        calendar.add(Calendar.SECOND, -windowSeconds);
        return alarmEventMapper.findMergeCandidate(tenantId, dedupKey, calendar.getTime());
    }
}
