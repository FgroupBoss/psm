package com.fgroupboss.ai.psm.location.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fgroupboss.ai.psm.location.model.entity.LocEventEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface LocEventMapper extends BaseMapper<LocEventEntity> {

    long countByTenant(@Param("tenantId") Long tenantId,
                       @Param("eventType") String eventType,
                       @Param("tagNo") String tagNo,
                       @Param("personId") Long personId,
                       @Param("fromTime") LocalDateTime fromTime,
                       @Param("toTime") LocalDateTime toTime);

    List<LocEventEntity> listByTenant(@Param("tenantId") Long tenantId,
                                      @Param("eventType") String eventType,
                                      @Param("tagNo") String tagNo,
                                      @Param("personId") Long personId,
                                      @Param("fromTime") LocalDateTime fromTime,
                                      @Param("toTime") LocalDateTime toTime,
                                      @Param("offset") int offset,
                                      @Param("limit") int limit);
}
