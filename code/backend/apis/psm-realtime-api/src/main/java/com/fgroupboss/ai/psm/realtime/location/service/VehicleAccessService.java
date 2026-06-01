package com.fgroupboss.ai.psm.realtime.location.service;

import com.fgroupboss.ai.psm.realtime.location.model.dto.VehicleRecordIngestRequest;
import com.fgroupboss.ai.psm.realtime.location.model.vo.VehicleAccessRecordVO;

public interface VehicleAccessService {

    VehicleAccessRecordVO ingest(VehicleRecordIngestRequest request);
}
