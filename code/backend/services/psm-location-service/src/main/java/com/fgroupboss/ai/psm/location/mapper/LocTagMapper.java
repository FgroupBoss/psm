package com.fgroupboss.ai.psm.location.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fgroupboss.ai.psm.location.model.entity.LocTagEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LocTagMapper extends BaseMapper<LocTagEntity> {

    long countByTenant(@Param("tenantId") Long tenantId,
                       @Param("keyword") String keyword,
                       @Param("status") String status);

    List<LocTagEntity> listByTenant(@Param("tenantId") Long tenantId,
                                      @Param("keyword") String keyword,
                                      @Param("status") String status,
                                      @Param("offset") int offset,
                                      @Param("limit") int limit);

    LocTagEntity findByTagNo(@Param("tenantId") Long tenantId, @Param("tagNo") String tagNo);
}
