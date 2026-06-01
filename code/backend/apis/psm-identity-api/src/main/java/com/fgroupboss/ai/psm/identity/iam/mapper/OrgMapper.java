package com.fgroupboss.ai.psm.identity.iam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fgroupboss.ai.psm.identity.iam.model.entity.OrgEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 组织持久化操作。
 */
public interface OrgMapper extends BaseMapper<OrgEntity> {

    OrgEntity findById(@Param("tenantId") Long tenantId, @Param("id") Long id);

    OrgEntity findByCode(@Param("tenantId") Long tenantId, @Param("orgCode") String orgCode);

    List<OrgEntity> tree(@Param("tenantId") Long tenantId);

    int updateOrg(OrgEntity entity);

    int softDelete(@Param("tenantId") Long tenantId, @Param("id") Long id);
}
