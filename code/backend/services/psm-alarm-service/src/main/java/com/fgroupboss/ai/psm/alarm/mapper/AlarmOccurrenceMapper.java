package com.fgroupboss.ai.psm.alarm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fgroupboss.ai.psm.alarm.model.entity.AlarmOccurrenceEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AlarmOccurrenceMapper extends BaseMapper<AlarmOccurrenceEntity> {

    List<AlarmOccurrenceEntity> listByAlarmEventId(@Param("tenantId") Long tenantId,
                                                   @Param("alarmEventId") Long alarmEventId);
}
