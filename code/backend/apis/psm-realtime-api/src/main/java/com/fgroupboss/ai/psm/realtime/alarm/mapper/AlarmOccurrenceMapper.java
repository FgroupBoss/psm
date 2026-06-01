package com.fgroupboss.ai.psm.realtime.alarm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fgroupboss.ai.psm.realtime.alarm.model.entity.AlarmOccurrenceEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AlarmOccurrenceMapper extends BaseMapper<AlarmOccurrenceEntity> {

    List<AlarmOccurrenceEntity> listByAlarmEventId(@Param("tenantId") Long tenantId,
                                                   @Param("alarmEventId") Long alarmEventId);
}
