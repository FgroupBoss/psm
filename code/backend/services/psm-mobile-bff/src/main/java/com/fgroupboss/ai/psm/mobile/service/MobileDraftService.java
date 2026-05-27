package com.fgroupboss.ai.psm.mobile.service;

import com.fgroupboss.ai.psm.mobile.client.WorkPermitClient;
import com.fgroupboss.ai.psm.mobile.client.dto.MobileDraftSyncRequest;
import com.fgroupboss.ai.psm.mobile.client.vo.MobileDraftSyncResultVO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 弱网草稿同步：优先委托 work-permit-service，不可用时本地内存兜底（演示）。
 */
@Service
@RequiredArgsConstructor
public class MobileDraftService {

    private final WorkPermitClient workPermitClient;
    private final ConcurrentHashMap<String, MobileDraftSyncResultVO> localDraftStore = new ConcurrentHashMap<String, MobileDraftSyncResultVO>();

    public MobileDraftSyncResultVO sync(MobileDraftSyncRequest request, HttpHeaders contextHeaders) {
        Optional<MobileDraftSyncResultVO> remote = workPermitClient.syncDraft(request, contextHeaders);
        if (remote.isPresent()) {
            return remote.get();
        }
        return storeLocally(request);
    }

    private MobileDraftSyncResultVO storeLocally(MobileDraftSyncRequest request) {
        MobileDraftSyncResultVO result = new MobileDraftSyncResultVO();
        result.setClientDraftId(request.getClientDraftId());
        result.setSyncStatus("PENDING");
        result.setStorage("BFF_LOCAL");
        result.setSyncedAt(new Date());
        localDraftStore.put(buildKey(request), result);
        return result;
    }

    private String buildKey(MobileDraftSyncRequest request) {
        return request.getTenantId() + ":" + request.getClientDraftId();
    }
}
