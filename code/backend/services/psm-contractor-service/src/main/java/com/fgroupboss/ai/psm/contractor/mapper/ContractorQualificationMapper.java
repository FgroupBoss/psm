package com.fgroupboss.ai.psm.contractor.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fgroupboss.ai.psm.contractor.model.entity.ContractorQualificationEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ContractorQualificationMapper extends BaseMapper<ContractorQualificationEntity> {

    List<ContractorQualificationEntity> listByCompany(@Param("tenantId") Long tenantId,
                                                      @Param("companyId") Long companyId);
}
