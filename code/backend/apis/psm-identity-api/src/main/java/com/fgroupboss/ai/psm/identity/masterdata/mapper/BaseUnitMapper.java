package com.fgroupboss.ai.psm.identity.masterdata.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fgroupboss.ai.psm.identity.masterdata.model.entity.BaseUnitEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 装置台账持久化操作。
 */
public interface BaseUnitMapper extends BaseMapper<BaseUnitEntity> {

    BaseUnitEntity findById(@Param("tenantId") Long tenantId, @Param("id") Long id);

    BaseUnitEntity findEnabledById(@Param("tenantId") Long tenantId, @Param("id") Long id);

    BaseUnitEntity findByCode(@Param("tenantId") Long tenantId, @Param("code") String code);

    List<BaseUnitEntity> list(@Param("tenantId") Long tenantId,
                              @Param("keyword") String keyword,
                              @Param("status") String status,
                              @Param("limit") int limit,
                              @Param("offset") int offset);

    long count(@Param("tenantId") Long tenantId,
               @Param("keyword") String keyword,
               @Param("status") String status);

    int updateRecord(BaseUnitEntity entity);

    int setStatus(@Param("tenantId") Long tenantId, @Param("id") Long id, @Param("status") String status);

    int softDelete(@Param("tenantId") Long tenantId, @Param("id") Long id);
}
