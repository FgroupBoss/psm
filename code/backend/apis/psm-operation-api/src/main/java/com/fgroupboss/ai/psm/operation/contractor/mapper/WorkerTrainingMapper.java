package com.fgroupboss.ai.psm.operation.contractor.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fgroupboss.ai.psm.operation.contractor.model.entity.WorkerTrainingRecordEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface WorkerTrainingMapper extends BaseMapper<WorkerTrainingRecordEntity> {

    List<WorkerTrainingRecordEntity> listByWorker(@Param("tenantId") Long tenantId, @Param("workerId") Long workerId);
}
