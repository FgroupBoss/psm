package com.fgroupboss.ai.psm.inspection.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fgroupboss.ai.psm.inspection.model.entity.ChecklistTemplateEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ChecklistTemplateMapper extends BaseMapper<ChecklistTemplateEntity> {

    long countByTenant(@Param("tenantId") Long tenantId, @Param("keyword") String keyword);

    List<ChecklistTemplateEntity> listByTenant(@Param("tenantId") Long tenantId,
                                               @Param("keyword") String keyword,
                                               @Param("offset") int offset,
                                               @Param("limit") int limit);

    ChecklistTemplateEntity findByCode(@Param("tenantId") Long tenantId, @Param("templateCode") String templateCode);
}
