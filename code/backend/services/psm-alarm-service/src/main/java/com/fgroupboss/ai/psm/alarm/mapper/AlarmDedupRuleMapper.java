package com.fgroupboss.ai.psm.alarm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fgroupboss.ai.psm.alarm.model.entity.AlarmDedupRuleEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AlarmDedupRuleMapper extends BaseMapper<AlarmDedupRuleEntity> {

    AlarmDedupRuleEntity findBySourceType(@Param("tenantId") Long tenantId,
                                          @Param("sourceType") String sourceType);
}
