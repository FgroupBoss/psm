package com.fgroupboss.ai.psm.iam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fgroupboss.ai.psm.iam.model.entity.IamUserEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * IAM 用户持久化操作。
 */
public interface IamUserMapper extends BaseMapper<IamUserEntity> {

    IamUserEntity findById(@Param("tenantId") Long tenantId, @Param("id") Long id);

    IamUserEntity findByUsername(@Param("tenantId") Long tenantId, @Param("username") String username);

    IamUserEntity findByAuthUserId(@Param("tenantId") Long tenantId, @Param("authUserId") Long authUserId);

    List<IamUserEntity> list(@Param("tenantId") Long tenantId,
                             @Param("keyword") String keyword,
                             @Param("limit") int limit,
                             @Param("offset") int offset);

    long count(@Param("tenantId") Long tenantId, @Param("keyword") String keyword);

    int updateUser(IamUserEntity entity);

    int updateStatus(@Param("tenantId") Long tenantId, @Param("id") Long id, @Param("status") String status);

    int increasePermissionVersion(@Param("tenantId") Long tenantId, @Param("id") Long id);
}
