package com.fgroupboss.ai.psm.realtime.video.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fgroupboss.ai.psm.realtime.video.model.entity.VideoAiEventEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface VideoAiEventMapper extends BaseMapper<VideoAiEventEntity> {

    long countByTenant(@Param("tenantId") Long tenantId,
                       @Param("cameraId") Long cameraId,
                       @Param("eventType") String eventType,
                       @Param("status") String status,
                       @Param("severity") String severity,
                       @Param("areaId") Long areaId,
                       @Param("occurredFrom") LocalDateTime occurredFrom,
                       @Param("occurredTo") LocalDateTime occurredTo);

    List<VideoAiEventEntity> listByTenant(@Param("tenantId") Long tenantId,
                                          @Param("cameraId") Long cameraId,
                                          @Param("eventType") String eventType,
                                          @Param("status") String status,
                                          @Param("severity") String severity,
                                          @Param("areaId") Long areaId,
                                          @Param("occurredFrom") LocalDateTime occurredFrom,
                                          @Param("occurredTo") LocalDateTime occurredTo,
                                          @Param("offset") int offset,
                                          @Param("limit") int limit);

    VideoAiEventEntity findRecentByDedupKey(@Param("tenantId") Long tenantId,
                                            @Param("dedupKey") String dedupKey,
                                            @Param("since") LocalDateTime since);
}
