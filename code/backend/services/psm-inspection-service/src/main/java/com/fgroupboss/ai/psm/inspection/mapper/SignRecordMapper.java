package com.fgroupboss.ai.psm.inspection.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fgroupboss.ai.psm.inspection.model.entity.SignRecordEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SignRecordMapper extends BaseMapper<SignRecordEntity> {

    List<SignRecordEntity> listByTask(@Param("tenantId") Long tenantId, @Param("taskId") Long taskId);

    SignRecordEntity findByTaskAndPoint(@Param("tenantId") Long tenantId,
                                        @Param("taskId") Long taskId,
                                        @Param("routePointId") Long routePointId);
}
