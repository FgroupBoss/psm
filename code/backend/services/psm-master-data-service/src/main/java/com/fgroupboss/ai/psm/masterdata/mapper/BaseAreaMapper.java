package com.fgroupboss.ai.psm.masterdata.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fgroupboss.ai.psm.masterdata.model.entity.BaseAreaEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 区域台账持久化操作。
 */
public interface BaseAreaMapper extends BaseMapper<BaseAreaEntity> {

    BaseAreaEntity findById(@Param("tenantId") Long tenantId, @Param("id") Long id);

    BaseAreaEntity findEnabledById(@Param("tenantId") Long tenantId, @Param("id") Long id);

    BaseAreaEntity findByCode(@Param("tenantId") Long tenantId, @Param("code") String code);

    List<BaseAreaEntity> list(@Param("tenantId") Long tenantId,
                              @Param("keyword") String keyword,
                              @Param("status") String status,
                              @Param("limit") int limit,
                              @Param("offset") int offset);

    long count(@Param("tenantId") Long tenantId,
               @Param("keyword") String keyword,
               @Param("status") String status);

    List<BaseAreaEntity> tree(@Param("tenantId") Long tenantId);

    int updateRecord(BaseAreaEntity entity);

    int setStatus(@Param("tenantId") Long tenantId, @Param("id") Long id, @Param("status") String status);

    int softDelete(@Param("tenantId") Long tenantId, @Param("id") Long id);
}
