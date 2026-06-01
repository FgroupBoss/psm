package com.fgroupboss.ai.psm.realtime.video.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fgroupboss.ai.psm.realtime.video.model.entity.VideoAiEventMediaEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface VideoAiEventMediaMapper extends BaseMapper<VideoAiEventMediaEntity> {

    List<VideoAiEventMediaEntity> listByEventId(@Param("tenantId") Long tenantId,
                                                @Param("eventId") Long eventId);
}
