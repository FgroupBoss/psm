package com.fgroupboss.ai.psm.identity.masterdata.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fgroupboss.ai.psm.identity.masterdata.model.entity.MasterDataItemEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 主数据条目持久化操作。
 */
public interface MasterDataItemMapper extends BaseMapper<MasterDataItemEntity> {

    MasterDataItemEntity findById(@Param("tenantId") Long tenantId,
                                  @Param("category") String category,
                                  @Param("id") Long id);

    MasterDataItemEntity findByCode(@Param("tenantId") Long tenantId,
                                    @Param("category") String category,
                                    @Param("code") String code);

    List<MasterDataItemEntity> list(@Param("tenantId") Long tenantId,
                                    @Param("category") String category,
                                    @Param("keyword") String keyword,
                                    @Param("limit") int limit,
                                    @Param("offset") int offset);

    long count(@Param("tenantId") Long tenantId,
               @Param("category") String category,
               @Param("keyword") String keyword);

    List<MasterDataItemEntity> tree(@Param("tenantId") Long tenantId,
                                    @Param("category") String category);

    int updateItem(MasterDataItemEntity entity);

    int setStatusDisabled(@Param("tenantId") Long tenantId,
                          @Param("category") String category,
                          @Param("id") Long id);

    int softDelete(@Param("tenantId") Long tenantId,
                   @Param("category") String category,
                   @Param("id") Long id);
}
