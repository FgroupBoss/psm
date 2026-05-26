package com.fgroupboss.ai.psm.contractor.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fgroupboss.ai.psm.contractor.model.entity.ContractorWorkerEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ContractorWorkerMapper extends BaseMapper<ContractorWorkerEntity> {

    long countByTenant(@Param("tenantId") Long tenantId,
                       @Param("companyId") Long companyId,
                       @Param("keyword") String keyword,
                       @Param("accessStatus") String accessStatus);

    List<ContractorWorkerEntity> listByTenant(@Param("tenantId") Long tenantId,
                                              @Param("companyId") Long companyId,
                                              @Param("keyword") String keyword,
                                              @Param("accessStatus") String accessStatus,
                                              @Param("offset") int offset,
                                              @Param("limit") int limit);

    ContractorWorkerEntity findByCode(@Param("tenantId") Long tenantId, @Param("workerCode") String workerCode);
}
