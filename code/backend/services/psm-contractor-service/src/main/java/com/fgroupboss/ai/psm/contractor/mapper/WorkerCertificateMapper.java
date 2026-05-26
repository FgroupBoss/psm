package com.fgroupboss.ai.psm.contractor.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fgroupboss.ai.psm.contractor.model.entity.WorkerCertificateEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface WorkerCertificateMapper extends BaseMapper<WorkerCertificateEntity> {

    List<WorkerCertificateEntity> listByWorker(@Param("tenantId") Long tenantId, @Param("workerId") Long workerId);
}
