package com.fgroupboss.ai.psm.inspection.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fgroupboss.ai.psm.inspection.model.entity.AbnormalRecordEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AbnormalRecordMapper extends BaseMapper<AbnormalRecordEntity> {

    List<AbnormalRecordEntity> listByTask(@Param("tenantId") Long tenantId, @Param("taskId") Long taskId);
}
