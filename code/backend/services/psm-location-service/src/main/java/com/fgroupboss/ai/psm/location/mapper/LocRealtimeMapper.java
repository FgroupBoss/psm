package com.fgroupboss.ai.psm.location.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fgroupboss.ai.psm.location.model.entity.LocRealtimeEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LocRealtimeMapper extends BaseMapper<LocRealtimeEntity> {

    List<LocRealtimeEntity> listByQuery(@Param("tenantId") Long tenantId,
                                        @Param("areaId") Long areaId,
                                        @Param("personId") Long personId,
                                        @Param("tagNo") String tagNo,
                                        @Param("onlineStatus") String onlineStatus);

    LocRealtimeEntity findByTagNo(@Param("tenantId") Long tenantId, @Param("tagNo") String tagNo);

    int countHeadcount(@Param("tenantId") Long tenantId,
                       @Param("areaId") Long areaId,
                       @Param("onlineStatus") String onlineStatus);
}
