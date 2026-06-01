package com.fgroupboss.ai.psm.realtime.video.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fgroupboss.ai.psm.realtime.video.model.entity.VideoCameraEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface VideoCameraMapper extends BaseMapper<VideoCameraEntity> {

    long countByTenant(@Param("tenantId") Long tenantId,
                       @Param("keyword") String keyword,
                       @Param("areaId") Long areaId,
                       @Param("status") String status);

    List<VideoCameraEntity> listByTenant(@Param("tenantId") Long tenantId,
                                         @Param("keyword") String keyword,
                                         @Param("areaId") Long areaId,
                                         @Param("status") String status,
                                         @Param("offset") int offset,
                                         @Param("limit") int limit);

    VideoCameraEntity findByCode(@Param("tenantId") Long tenantId,
                                 @Param("cameraCode") String cameraCode);
}
