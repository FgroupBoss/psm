package com.fgroupboss.ai.psm.operation.contractor.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fgroupboss.ai.psm.operation.contractor.model.entity.ContractorBlacklistEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ContractorBlacklistMapper extends BaseMapper<ContractorBlacklistEntity> {

    ContractorBlacklistEntity findActiveByTarget(@Param("tenantId") Long tenantId,
                                                 @Param("targetType") String targetType,
                                                 @Param("targetId") Long targetId);
}
