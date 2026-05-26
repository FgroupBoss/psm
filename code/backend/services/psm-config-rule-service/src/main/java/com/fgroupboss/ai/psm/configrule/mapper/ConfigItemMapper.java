package com.fgroupboss.ai.psm.configrule.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fgroupboss.ai.psm.configrule.model.entity.ConfigItemEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 配置项持久化操作。
 */
public interface ConfigItemMapper extends BaseMapper<ConfigItemEntity> {

    ConfigItemEntity findById(@Param("tenantId") Long tenantId, @Param("id") Long id);

    ConfigItemEntity findLatestByCode(@Param("tenantId") Long tenantId,
                                      @Param("configType") String configType,
                                      @Param("configCode") String configCode);

    List<ConfigItemEntity> list(@Param("tenantId") Long tenantId,
                                @Param("configType") String configType,
                                @Param("keyword") String keyword,
                                @Param("status") String status,
                                @Param("bizScene") String bizScene,
                                @Param("limit") int limit,
                                @Param("offset") int offset);

    long count(@Param("tenantId") Long tenantId,
               @Param("configType") String configType,
               @Param("keyword") String keyword,
               @Param("status") String status,
               @Param("bizScene") String bizScene);

    List<ConfigItemEntity> findPublishedRules(@Param("tenantId") Long tenantId,
                                              @Param("scene") String scene,
                                              @Param("ruleCode") String ruleCode);

    int updateDraft(ConfigItemEntity entity);

    int setStatus(@Param("tenantId") Long tenantId,
                  @Param("id") Long id,
                  @Param("status") String status);

    int softDelete(@Param("tenantId") Long tenantId, @Param("id") Long id);
}
