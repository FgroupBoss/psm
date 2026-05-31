package com.fgroupboss.ai.psm.realtime.location.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fgroupboss.ai.psm.realtime.location.model.entity.LocTagBindingEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface LocTagBindingMapper extends BaseMapper<LocTagBindingEntity> {

    LocTagBindingEntity findActiveByTagNo(@Param("tenantId") Long tenantId, @Param("tagNo") String tagNo);
}
