package com.fgroupboss.ai.psm.majorhazard.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fgroupboss.ai.psm.majorhazard.model.entity.MajorHazardAttachmentEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MajorHazardAttachmentMapper extends BaseMapper<MajorHazardAttachmentEntity> {

    List<MajorHazardAttachmentEntity> listByHazardId(@Param("tenantId") Long tenantId, @Param("hazardId") Long hazardId);
}
