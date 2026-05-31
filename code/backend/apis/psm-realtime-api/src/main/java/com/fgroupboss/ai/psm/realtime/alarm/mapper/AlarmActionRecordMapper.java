package com.fgroupboss.ai.psm.realtime.alarm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fgroupboss.ai.psm.realtime.alarm.model.entity.AlarmActionRecordEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AlarmActionRecordMapper extends BaseMapper<AlarmActionRecordEntity> {

    List<AlarmActionRecordEntity> listByAlarmEventId(@Param("tenantId") Long tenantId,
                                                     @Param("alarmEventId") Long alarmEventId);
}
