package com.fgroupboss.ai.psm.location.service;

import com.fgroupboss.ai.psm.location.model.dto.VehicleRecordIngestRequest;
import com.fgroupboss.ai.psm.location.model.vo.VehicleAccessRecordVO;

public interface VehicleAccessService {

    VehicleAccessRecordVO ingest(VehicleRecordIngestRequest request);
}
