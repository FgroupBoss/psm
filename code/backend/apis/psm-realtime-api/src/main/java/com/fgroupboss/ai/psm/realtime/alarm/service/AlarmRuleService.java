package com.fgroupboss.ai.psm.realtime.alarm.service;

import com.fgroupboss.ai.psm.realtime.alarm.model.dto.AlarmRuleSaveRequest;
import com.fgroupboss.ai.psm.realtime.alarm.model.vo.AlarmRuleVO;

import java.util.List;

public interface AlarmRuleService {

    List<AlarmRuleVO> list(Long tenantId, String ruleType);

    AlarmRuleVO get(Long tenantId, Long id);

    AlarmRuleVO create(AlarmRuleSaveRequest request);

    AlarmRuleVO update(Long id, AlarmRuleSaveRequest request);
}
