package com.fgroupboss.ai.psm.realtime.alarm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fgroupboss.ai.psm.realtime.alarm.model.entity.AlarmEventEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface AlarmEventMapper extends BaseMapper<AlarmEventEntity> {

    long countByTenant(@Param("tenantId") Long tenantId,
                       @Param("keyword") String keyword,
                       @Param("status") String status,
                       @Param("alarmLevel") String alarmLevel,
                       @Param("areaId") Long areaId,
                       @Param("hazardId") Long hazardId,
                       @Param("sourceType") String sourceType,
                       @Param("occurredFrom") Date occurredFrom,
                       @Param("occurredTo") Date occurredTo);

    List<AlarmEventEntity> listByTenant(@Param("tenantId") Long tenantId,
                                        @Param("keyword") String keyword,
                                        @Param("status") String status,
                                        @Param("alarmLevel") String alarmLevel,
                                        @Param("areaId") Long areaId,
                                        @Param("hazardId") Long hazardId,
                                        @Param("sourceType") String sourceType,
                                        @Param("occurredFrom") Date occurredFrom,
                                        @Param("occurredTo") Date occurredTo,
                                        @Param("offset") int offset,
                                        @Param("limit") int limit);

    AlarmEventEntity findMergeCandidate(@Param("tenantId") Long tenantId,
                                        @Param("dedupKey") String dedupKey,
                                        @Param("windowStart") Date windowStart);

    List<AlarmEventEntity> listActiveByArea(@Param("tenantId") Long tenantId,
                                            @Param("areaId") Long areaId);

    List<AlarmEventEntity> listTimeoutCandidates(@Param("tenantId") Long tenantId,
                                                 @Param("status") String status,
                                                 @Param("alarmLevel") String alarmLevel,
                                                 @Param("deadline") Date deadline);

    List<AlarmEventEntity> listDisposeTimeoutCandidates(@Param("tenantId") Long tenantId,
                                                       @Param("alarmLevel") String alarmLevel,
                                                       @Param("deadline") Date deadline);
}
