package com.fgroupboss.ai.psm.risk.inspection.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fgroupboss.ai.psm.risk.inspection.model.entity.TaskItemEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TaskItemMapper extends BaseMapper<TaskItemEntity> {

    List<TaskItemEntity> listByTask(@Param("tenantId") Long tenantId, @Param("taskId") Long taskId);

    TaskItemEntity findByTaskAndChecklistItem(@Param("tenantId") Long tenantId,
                                              @Param("taskId") Long taskId,
                                              @Param("checklistItemId") Long checklistItemId);

    long countChecked(@Param("tenantId") Long tenantId, @Param("taskId") Long taskId);

    long countAbnormal(@Param("tenantId") Long tenantId, @Param("taskId") Long taskId);
}
