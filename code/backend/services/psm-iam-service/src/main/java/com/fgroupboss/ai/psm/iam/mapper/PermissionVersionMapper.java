package com.fgroupboss.ai.psm.iam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fgroupboss.ai.psm.iam.model.entity.PermissionVersionEntity;
import org.apache.ibatis.annotations.Param;

/**
 * 权限版本持久化操作。
 */
public interface PermissionVersionMapper extends BaseMapper<PermissionVersionEntity> {

    PermissionVersionEntity findByUser(@Param("tenantId") Long tenantId, @Param("userId") Long userId);

    int increaseUserVersion(@Param("tenantId") Long tenantId,
                            @Param("userId") Long userId,
                            @Param("changedReason") String changedReason);
}
