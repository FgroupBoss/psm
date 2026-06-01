package com.fgroupboss.ai.psm.operation.workpermit.blindplate.service;

import com.fgroupboss.ai.psm.operation.api.workpermit.dto.BlindPlateActionConfirmRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.BlindPlateRegistryRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.BlindPlateWorkDetailRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.BlindPlateActionRecordVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.BlindPlateFlowProgressVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.BlindPlatePreCheckResultVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.BlindPlateRegistryVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.BlindPlateWorkDetailVO;
import com.fgroupboss.ai.psm.operation.workpermit.blindplate.config.BlindPlateCheckPoint;
import com.fgroupboss.ai.psm.operation.workpermit.model.entity.WorkPermitEntity;
import com.fgroupboss.ai.psm.operation.workpermit.model.vo.PreCheckResultVO;

import java.util.List;

public interface BlindPlateService {

    List<BlindPlateRegistryVO> listRegistry(Long tenantId, String status);

    BlindPlateRegistryVO getRegistry(Long tenantId, Long registryId);

    BlindPlateRegistryVO createRegistry(Long tenantId, BlindPlateRegistryRequest request, String operator);

    BlindPlateRegistryVO updateRegistry(Long tenantId, Long registryId, BlindPlateRegistryRequest request, String operator);

    BlindPlateWorkDetailVO getDetail(Long tenantId, Long permitId);

    BlindPlateWorkDetailVO saveDetail(Long tenantId, Long permitId, BlindPlateWorkDetailRequest request, String operator);

    List<BlindPlateActionRecordVO> listActionRecords(Long tenantId, Long permitId);

    BlindPlateActionRecordVO confirmAction(Long tenantId, Long permitId,
                                           BlindPlateActionConfirmRequest request, String operator);

    BlindPlatePreCheckResultVO preCheck(Long tenantId, Long permitId, String checkPoint);

    BlindPlateFlowProgressVO getFlowProgress(Long tenantId, Long permitId);

    void applyPreCheck(WorkPermitEntity permit, BlindPlateCheckPoint checkPoint, PreCheckResultVO result);
}
