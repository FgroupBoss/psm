package com.fgroupboss.ai.psm.location.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fgroupboss.ai.psm.location.model.entity.GateAccessRecordEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface GateAccessRecordMapper extends BaseMapper<GateAccessRecordEntity> {

    long countByTenant(@Param("tenantId") Long tenantId,
                       @Param("gateCode") String gateCode,
                       @Param("personId") Long personId,
                       @Param("fromTime") LocalDateTime fromTime,
                       @Param("toTime") LocalDateTime toTime);

    List<GateAccessRecordEntity> listByTenant(@Param("tenantId") Long tenantId,
                                              @Param("gateCode") String gateCode,
                                              @Param("personId") Long personId,
                                              @Param("fromTime") LocalDateTime fromTime,
                                              @Param("toTime") LocalDateTime toTime,
                                              @Param("offset") int offset,
                                              @Param("limit") int limit);
}
