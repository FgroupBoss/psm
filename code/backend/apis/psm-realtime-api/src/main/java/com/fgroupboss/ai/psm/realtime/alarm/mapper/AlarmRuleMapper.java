package com.fgroupboss.ai.psm.realtime.alarm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fgroupboss.ai.psm.realtime.alarm.model.entity.AlarmRuleEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AlarmRuleMapper extends BaseMapper<AlarmRuleEntity> {

    List<AlarmRuleEntity> listByTenant(@Param("tenantId") Long tenantId,
                                       @Param("ruleType") String ruleType);

    List<AlarmRuleEntity> listEnabledByType(@Param("ruleType") String ruleType);

    AlarmRuleEntity findByCode(@Param("tenantId") Long tenantId, @Param("ruleCode") String ruleCode);
}
