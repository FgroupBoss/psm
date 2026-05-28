package com.fgroupboss.ai.psm.inspection.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fgroupboss.ai.psm.inspection.model.entity.ChecklistItemEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ChecklistItemMapper extends BaseMapper<ChecklistItemEntity> {

    List<ChecklistItemEntity> listByTemplate(@Param("tenantId") Long tenantId, @Param("templateId") Long templateId);

    int softDeleteByTemplate(@Param("tenantId") Long tenantId, @Param("templateId") Long templateId);
}
