package com.fgroupboss.ai.psm.iam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fgroupboss.ai.psm.iam.model.entity.PostEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 岗位持久化操作。
 */
public interface PostMapper extends BaseMapper<PostEntity> {

    PostEntity findById(@Param("tenantId") Long tenantId, @Param("id") Long id);

    PostEntity findByCode(@Param("tenantId") Long tenantId, @Param("postCode") String postCode);

    List<PostEntity> list(@Param("tenantId") Long tenantId,
                          @Param("keyword") String keyword,
                          @Param("limit") int limit,
                          @Param("offset") int offset);

    long count(@Param("tenantId") Long tenantId, @Param("keyword") String keyword);

    int updatePost(PostEntity entity);

    int softDelete(@Param("tenantId") Long tenantId, @Param("id") Long id);
}
