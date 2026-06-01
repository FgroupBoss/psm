package com.fgroupboss.ai.psm.operation.contractor.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fgroupboss.ai.psm.operation.contractor.model.entity.ContractorCompanyEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ContractorCompanyMapper extends BaseMapper<ContractorCompanyEntity> {

    long countByTenant(@Param("tenantId") Long tenantId,
                       @Param("keyword") String keyword,
                       @Param("status") String status);

    List<ContractorCompanyEntity> listByTenant(@Param("tenantId") Long tenantId,
                                               @Param("keyword") String keyword,
                                               @Param("status") String status,
                                               @Param("offset") int offset,
                                               @Param("limit") int limit);

    ContractorCompanyEntity findByCode(@Param("tenantId") Long tenantId,
                                       @Param("companyCode") String companyCode);
}
