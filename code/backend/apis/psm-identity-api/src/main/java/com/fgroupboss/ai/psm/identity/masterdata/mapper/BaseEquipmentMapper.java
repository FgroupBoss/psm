package com.fgroupboss.ai.psm.identity.masterdata.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fgroupboss.ai.psm.identity.masterdata.model.entity.BaseEquipmentEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 设备台账持久化操作。
 */
public interface BaseEquipmentMapper extends BaseMapper<BaseEquipmentEntity> {

    BaseEquipmentEntity findById(@Param("tenantId") Long tenantId, @Param("id") Long id);

    BaseEquipmentEntity findEnabledById(@Param("tenantId") Long tenantId, @Param("id") Long id);

    BaseEquipmentEntity findByCode(@Param("tenantId") Long tenantId, @Param("code") String code);

    List<BaseEquipmentEntity> list(@Param("tenantId") Long tenantId,
                                   @Param("keyword") String keyword,
                                   @Param("status") String status,
                                   @Param("limit") int limit,
                                   @Param("offset") int offset);

    long count(@Param("tenantId") Long tenantId,
               @Param("keyword") String keyword,
               @Param("status") String status);

    int updateRecord(BaseEquipmentEntity entity);

    int setStatus(@Param("tenantId") Long tenantId, @Param("id") Long id, @Param("status") String status);

    int softDelete(@Param("tenantId") Long tenantId, @Param("id") Long id);
}
