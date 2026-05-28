package com.fgroupboss.ai.psm.video.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fgroupboss.ai.psm.video.model.entity.VideoWatchSessionEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface VideoWatchSessionMapper extends BaseMapper<VideoWatchSessionEntity> {

    VideoWatchSessionEntity findActiveByPermit(@Param("tenantId") Long tenantId,
                                               @Param("workPermitId") Long workPermitId);
}
