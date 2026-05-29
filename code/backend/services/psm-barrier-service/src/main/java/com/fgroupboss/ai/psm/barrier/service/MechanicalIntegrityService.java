package com.fgroupboss.ai.psm.barrier.service;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.barrier.model.dto.MiDefectCloseRequest;
import com.fgroupboss.ai.psm.barrier.model.dto.MiDefectRequest;
import com.fgroupboss.ai.psm.barrier.model.dto.MiEquipmentRequest;
import com.fgroupboss.ai.psm.barrier.model.dto.MiInspectionPlanRequest;
import com.fgroupboss.ai.psm.barrier.model.vo.MiDefectVO;
import com.fgroupboss.ai.psm.barrier.model.vo.MiEquipmentVO;
import com.fgroupboss.ai.psm.barrier.model.vo.MiInspectionPlanVO;

/**
 * 机械完整性：关键设备、检验计划与缺陷闭环。
 */
public interface MechanicalIntegrityService {

    PageResult<MiEquipmentVO> pageEquipment(Long tenantId, String keyword, int pageNo, int pageSize);

    MiEquipmentVO createEquipment(MiEquipmentRequest request, String operator);

    MiEquipmentVO updateEquipment(Long id, MiEquipmentRequest request, String operator);

    PageResult<MiInspectionPlanVO> pageInspectionPlans(Long tenantId, Long equipmentId, int pageNo, int pageSize);

    MiInspectionPlanVO createInspectionPlan(MiInspectionPlanRequest request, String operator);

    PageResult<MiDefectVO> pageDefects(Long tenantId, Long equipmentId, String status, int pageNo, int pageSize);

    MiDefectVO createDefect(MiDefectRequest request, String operator);

    MiDefectVO closeDefect(Long id, MiDefectCloseRequest request, String operator);
}
