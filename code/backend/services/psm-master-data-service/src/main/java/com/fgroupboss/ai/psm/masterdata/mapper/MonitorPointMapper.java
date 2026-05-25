package com.fgroupboss.ai.psm.masterdata.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fgroupboss.ai.psm.masterdata.model.entity.MonitorPointEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 监测点位台账持久化操作。
 */
public interface MonitorPointMapper extends BaseMapper<MonitorPointEntity> {

    MonitorPointEntity findById(@Param("tenantId") Long tenantId, @Param("id") Long id);

    MonitorPointEntity findEnabledById(@Param("tenantId") Long tenantId, @Param("id") Long id);

    MonitorPointEntity findByCode(@Param("tenantId") Long tenantId, @Param("code") String code);

    List<MonitorPointEntity> list(@Param("tenantId") Long tenantId,
                                  @Param("keyword") String keyword,
                                  @Param("status") String status,
                                  @Param("limit") int limit,
                                  @Param("offset") int offset);

    long count(@Param("tenantId") Long tenantId,
               @Param("keyword") String keyword,
               @Param("status") String status);

    int updateRecord(MonitorPointEntity entity);

    int setStatus(@Param("tenantId") Long tenantId, @Param("id") Long id, @Param("status") String status);

    int softDelete(@Param("tenantId") Long tenantId, @Param("id") Long id);
}
