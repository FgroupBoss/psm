package com.fgroupboss.ai.psm.majorhazard.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fgroupboss.ai.psm.majorhazard.model.entity.MajorHazardEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MajorHazardMapper extends BaseMapper<MajorHazardEntity> {

    long countByTenant(@Param("tenantId") Long tenantId,
                       @Param("keyword") String keyword,
                       @Param("status") String status,
                       @Param("level") String level);

    List<MajorHazardEntity> listByTenant(@Param("tenantId") Long tenantId,
                                         @Param("keyword") String keyword,
                                         @Param("status") String status,
                                         @Param("level") String level,
                                         @Param("offset") int offset,
                                         @Param("limit") int limit);
}
