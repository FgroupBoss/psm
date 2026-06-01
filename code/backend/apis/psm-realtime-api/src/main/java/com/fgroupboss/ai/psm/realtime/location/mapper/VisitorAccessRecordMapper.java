package com.fgroupboss.ai.psm.realtime.location.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fgroupboss.ai.psm.realtime.location.model.entity.VisitorAccessRecordEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface VisitorAccessRecordMapper extends BaseMapper<VisitorAccessRecordEntity> {

    long countByTenant(@Param("tenantId") Long tenantId,
                       @Param("visitorName") String visitorName,
                       @Param("gateCode") String gateCode,
                       @Param("fromTime") LocalDateTime fromTime,
                       @Param("toTime") LocalDateTime toTime);

    List<VisitorAccessRecordEntity> listByTenant(@Param("tenantId") Long tenantId,
                                                 @Param("visitorName") String visitorName,
                                                 @Param("gateCode") String gateCode,
                                                 @Param("fromTime") LocalDateTime fromTime,
                                                 @Param("toTime") LocalDateTime toTime,
                                                 @Param("offset") int offset,
                                                 @Param("limit") int limit);
}
