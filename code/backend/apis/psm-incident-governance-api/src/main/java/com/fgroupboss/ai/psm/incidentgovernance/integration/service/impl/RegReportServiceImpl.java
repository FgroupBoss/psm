package com.fgroupboss.ai.psm.incidentgovernance.integration.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.incidentgovernance.integration.config.RegRetryStatus;
import com.fgroupboss.ai.psm.incidentgovernance.integration.config.RegTaskStatus;
import com.fgroupboss.ai.psm.incidentgovernance.integration.config.RegTriggerType;
import com.fgroupboss.ai.psm.incidentgovernance.integration.mapper.RegCodeMappingMapper;
import com.fgroupboss.ai.psm.incidentgovernance.integration.mapper.RegFieldMappingMapper;
import com.fgroupboss.ai.psm.incidentgovernance.integration.mapper.RegReportReceiptMapper;
import com.fgroupboss.ai.psm.incidentgovernance.integration.mapper.RegReportTaskMapper;
import com.fgroupboss.ai.psm.incidentgovernance.integration.mapper.RegRetryQueueMapper;
import com.fgroupboss.ai.psm.incidentgovernance.integration.model.dto.RegReportPreviewRequest;
import com.fgroupboss.ai.psm.incidentgovernance.integration.model.dto.RegReportTriggerRequest;
import com.fgroupboss.ai.psm.incidentgovernance.integration.model.entity.RegCodeMappingEntity;
import com.fgroupboss.ai.psm.incidentgovernance.integration.model.entity.RegFieldMappingEntity;
import com.fgroupboss.ai.psm.incidentgovernance.integration.model.entity.RegReportReceiptEntity;
import com.fgroupboss.ai.psm.incidentgovernance.integration.model.entity.RegReportTaskEntity;
import com.fgroupboss.ai.psm.incidentgovernance.integration.model.entity.RegRetryQueueEntity;
import com.fgroupboss.ai.psm.incidentgovernance.integration.model.vo.RegReconciliationVO;
import com.fgroupboss.ai.psm.incidentgovernance.integration.model.vo.RegReportPreviewVO;
import com.fgroupboss.ai.psm.incidentgovernance.integration.model.vo.RegReportReceiptVO;
import com.fgroupboss.ai.psm.incidentgovernance.integration.model.vo.RegReportTaskVO;
import com.fgroupboss.ai.psm.incidentgovernance.integration.service.RegBusinessDataExtractor;
import com.fgroupboss.ai.psm.incidentgovernance.integration.service.RegPlatformConfigService;
import com.fgroupboss.ai.psm.incidentgovernance.integration.service.RegReportService;
import com.fgroupboss.ai.psm.incidentgovernance.integration.service.support.RegPayloadBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 监管上报任务：触发、模拟上报、重试与对账。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RegReportServiceImpl implements RegReportService {

    private static final String MOCK_RECEIPT_CODE = "0";
    private static final String MOCK_RECEIPT_MESSAGE = "mock accept success";

    private final RegReportTaskMapper reportTaskMapper;
    private final RegReportReceiptMapper reportReceiptMapper;
    private final RegRetryQueueMapper retryQueueMapper;
    private final RegFieldMappingMapper fieldMappingMapper;
    private final RegCodeMappingMapper codeMappingMapper;
    private final RegPlatformConfigService platformConfigService;
    private final RegBusinessDataExtractor businessDataExtractor;

    @Override
    public PageResult<RegReportTaskVO> pageTasks(Long tenantId, String platformCode, String dataDomain,
                                                 String status, int pageNo, int pageSize) {
        requireTenantId(tenantId);
        int normalizedPageNo = Math.max(pageNo, 1);
        int normalizedPageSize = Math.min(Math.max(pageSize, 1), 200);

        LambdaQueryWrapper<RegReportTaskEntity> wrapper = new LambdaQueryWrapper<RegReportTaskEntity>();
        wrapper.eq(RegReportTaskEntity::getTenantId, tenantId);
        if (StringUtils.hasText(platformCode)) {
            wrapper.eq(RegReportTaskEntity::getPlatformCode, platformCode.trim());
        }
        if (StringUtils.hasText(dataDomain)) {
            wrapper.eq(RegReportTaskEntity::getDataDomain, dataDomain.trim());
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(RegReportTaskEntity::getStatus, status.trim());
        }
        wrapper.orderByDesc(RegReportTaskEntity::getId);

        Page<RegReportTaskEntity> mpPage = new Page<RegReportTaskEntity>(normalizedPageNo, normalizedPageSize);
        Page<RegReportTaskEntity> result = reportTaskMapper.selectPage(mpPage, wrapper);

        List<RegReportTaskVO> records = new ArrayList<RegReportTaskVO>();
        for (RegReportTaskEntity entity : result.getRecords()) {
            records.add(toTaskVO(entity));
        }
        return new PageResult<RegReportTaskVO>(result.getTotal(), normalizedPageNo, normalizedPageSize, records);
    }

    @Override
    @Transactional
    public RegReportTaskVO trigger(RegReportTriggerRequest request) {
        requireTenantId(request.getTenantId());
        platformConfigService.requireEnabledPlatform(request.getTenantId(), request.getPlatformCode());

        RegReportTaskEntity task = createTask(request);
        return executeReport(task);
    }

    @Override
    @Transactional
    public RegReportTaskVO retry(Long tenantId, Long taskId) {
        requireTenantId(tenantId);
        RegReportTaskEntity task = requireTask(tenantId, taskId);
        if (!RegTaskStatus.FAILED.name().equals(task.getStatus())
                && !RegTaskStatus.PARTIAL_SUCCESS.name().equals(task.getStatus())) {
            throw new BusinessException(400, "only failed tasks can be retried");
        }
        RegRetryQueueEntity queue = findRetryQueue(tenantId, taskId);
        if (queue != null) {
            queue.setRetryCount(queue.getRetryCount() + 1);
            queue.setStatus(RegRetryStatus.PROCESSING.name());
            retryQueueMapper.updateById(queue);
        }
        task.setStatus(RegTaskStatus.RUNNING.name());
        reportTaskMapper.updateById(task);
        log.info("reg report retry tenantId={} taskId={} taskNo={}", tenantId, taskId, task.getTaskNo());
        return executeReport(task);
    }

    @Override
    public PageResult<RegReportReceiptVO> pageReceipts(Long tenantId, Long taskId, int pageNo, int pageSize) {
        requireTenantId(tenantId);
        int normalizedPageNo = Math.max(pageNo, 1);
        int normalizedPageSize = Math.min(Math.max(pageSize, 1), 200);

        LambdaQueryWrapper<RegReportReceiptEntity> wrapper = new LambdaQueryWrapper<RegReportReceiptEntity>();
        wrapper.eq(RegReportReceiptEntity::getTenantId, tenantId);
        if (taskId != null) {
            wrapper.eq(RegReportReceiptEntity::getTaskId, taskId);
        }
        wrapper.orderByDesc(RegReportReceiptEntity::getId);

        Page<RegReportReceiptEntity> mpPage = new Page<RegReportReceiptEntity>(normalizedPageNo, normalizedPageSize);
        Page<RegReportReceiptEntity> result = reportReceiptMapper.selectPage(mpPage, wrapper);

        Map<Long, String> taskNoCache = new LinkedHashMap<Long, String>();
        List<RegReportReceiptVO> records = new ArrayList<RegReportReceiptVO>();
        for (RegReportReceiptEntity entity : result.getRecords()) {
            records.add(toReceiptVO(entity, taskNoCache));
        }
        return new PageResult<RegReportReceiptVO>(result.getTotal(), normalizedPageNo, normalizedPageSize, records);
    }

    @Override
    public RegReconciliationVO reconciliation(Long tenantId, String platformCode, String dataDomain) {
        requireTenantId(tenantId);
        LambdaQueryWrapper<RegReportTaskEntity> wrapper = new LambdaQueryWrapper<RegReportTaskEntity>();
        wrapper.eq(RegReportTaskEntity::getTenantId, tenantId);
        if (StringUtils.hasText(platformCode)) {
            wrapper.eq(RegReportTaskEntity::getPlatformCode, platformCode.trim());
        }
        if (StringUtils.hasText(dataDomain)) {
            wrapper.eq(RegReportTaskEntity::getDataDomain, dataDomain.trim());
        }
        List<RegReportTaskEntity> tasks = reportTaskMapper.selectList(wrapper);

        long total = tasks.size();
        long success = 0;
        long failed = 0;
        Map<String, RegReconciliationVO.RegReconciliationItemVO> itemMap = new LinkedHashMap<String, RegReconciliationVO.RegReconciliationItemVO>();

        for (RegReportTaskEntity task : tasks) {
            if (RegTaskStatus.SUCCESS.name().equals(task.getStatus())) {
                success++;
            } else if (RegTaskStatus.FAILED.name().equals(task.getStatus())) {
                failed++;
            }
            String key = task.getPlatformCode() + "|" + task.getDataDomain();
            RegReconciliationVO.RegReconciliationItemVO item = itemMap.get(key);
            if (item == null) {
                item = new RegReconciliationVO.RegReconciliationItemVO();
                item.setPlatformCode(task.getPlatformCode());
                item.setDataDomain(task.getDataDomain());
                itemMap.put(key, item);
            }
            item.setTotal(item.getTotal() + 1);
            if (RegTaskStatus.SUCCESS.name().equals(task.getStatus())) {
                item.setSuccess(item.getSuccess() + 1);
            } else if (RegTaskStatus.FAILED.name().equals(task.getStatus())) {
                item.setFailed(item.getFailed() + 1);
            }
        }

        for (RegReconciliationVO.RegReconciliationItemVO item : itemMap.values()) {
            item.setSuccessRate(calcRate(item.getSuccess(), item.getTotal()));
        }

        RegReconciliationVO vo = new RegReconciliationVO();
        vo.setTenantId(tenantId);
        vo.setTotalTasks(total);
        vo.setSuccessTasks(success);
        vo.setFailedTasks(failed);
        vo.setSuccessRate(calcRate(success, total));
        vo.setItems(new ArrayList<RegReconciliationVO.RegReconciliationItemVO>(itemMap.values()));
        return vo;
    }

    @Override
    public RegReportPreviewVO preview(RegReportPreviewRequest request) {
        requireTenantId(request.getTenantId());
        platformConfigService.requireEnabledPlatform(request.getTenantId(), request.getPlatformCode());

        List<RegFieldMappingEntity> fieldMappings = loadFieldMappings(request.getTenantId(),
                request.getPlatformCode(), request.getDataDomain());
        List<RegCodeMappingEntity> codeMappings = loadCodeMappings(request.getTenantId(), request.getPlatformCode());

        List<Map<String, Object>> sourceRecords = businessDataExtractor.extract(
                request.getTenantId(), request.getDataDomain().trim());
        Map<String, Object> payload = RegPayloadBuilder.buildPayload(
                request.getTenantId(),
                request.getPlatformCode().trim(),
                request.getDataDomain().trim(),
                request.getDataWindowStart(),
                request.getDataWindowEnd(),
                fieldMappings,
                codeMappings,
                sourceRecords);

        RegReportPreviewVO vo = new RegReportPreviewVO();
        vo.setPlatformCode(request.getPlatformCode().trim());
        vo.setDataDomain(request.getDataDomain().trim());
        vo.setRecordCount(((Number) payload.get("recordCount")).intValue());
        vo.setPayloadDigest(RegPayloadBuilder.digest(payload));
        vo.setPayload(payload);
        return vo;
    }

    private RegReportTaskVO executeReport(RegReportTaskEntity task) {
        List<RegFieldMappingEntity> fieldMappings = loadFieldMappings(
                task.getTenantId(), task.getPlatformCode(), task.getDataDomain());
        List<RegCodeMappingEntity> codeMappings = loadCodeMappings(task.getTenantId(), task.getPlatformCode());

        List<Map<String, Object>> sourceRecords = businessDataExtractor.extract(
                task.getTenantId(), task.getDataDomain());
        Map<String, Object> payload = RegPayloadBuilder.buildPayload(
                task.getTenantId(),
                task.getPlatformCode(),
                task.getDataDomain(),
                task.getDataWindowStart(),
                task.getDataWindowEnd(),
                fieldMappings,
                codeMappings,
                sourceRecords);

        String digest = RegPayloadBuilder.digest(payload);
        int recordCount = ((Number) payload.get("recordCount")).intValue();
        LocalDateTime now = LocalDateTime.now();

        task.setStatus(RegTaskStatus.RUNNING.name());
        task.setPayloadDigest(digest);
        task.setRecordCount(recordCount);
        reportTaskMapper.updateById(task);

        // 二期：基于真实抽取组包后模拟监管回执（传输适配待联调）
        task.setStatus(RegTaskStatus.SUCCESS.name());
        task.setExecutedAt(now);
        task.setRecordCount(recordCount);
        task.setPayloadDigest(digest);
        reportTaskMapper.updateById(task);

        saveMockReceipt(task, now);
        markRetryDone(task.getTenantId(), task.getId());

        log.info("reg report simulated success tenantId={} taskNo={} digest={}", task.getTenantId(),
                task.getTaskNo(), digest);
        return toTaskVO(task);
    }

    private RegReportTaskEntity createTask(RegReportTriggerRequest request) {
        RegReportTaskEntity task = new RegReportTaskEntity();
        task.setTenantId(request.getTenantId());
        task.setTaskNo(generateTaskNo(request.getTenantId()));
        task.setPlatformCode(request.getPlatformCode().trim());
        task.setDataDomain(request.getDataDomain().trim());
        task.setTriggerType(resolveTriggerType(request.getTriggerType()));
        task.setDataWindowStart(request.getDataWindowStart());
        task.setDataWindowEnd(request.getDataWindowEnd());
        task.setRecordCount(0);
        task.setStatus(RegTaskStatus.PENDING.name());
        reportTaskMapper.insert(task);
        return task;
    }

    private void saveMockReceipt(RegReportTaskEntity task, LocalDateTime receiptTime) {
        RegReportReceiptEntity receipt = new RegReportReceiptEntity();
        receipt.setTenantId(task.getTenantId());
        receipt.setTaskId(task.getId());
        receipt.setSuccess(1);
        receipt.setPlatformCode(MOCK_RECEIPT_CODE);
        receipt.setPlatformMessage(MOCK_RECEIPT_MESSAGE);
        receipt.setReceiptTime(receiptTime);
        reportReceiptMapper.insert(receipt);
    }

    private void markRetryDone(Long tenantId, Long taskId) {
        RegRetryQueueEntity queue = findRetryQueue(tenantId, taskId);
        if (queue != null) {
            queue.setStatus(RegRetryStatus.DONE.name());
            queue.setLastError(null);
            retryQueueMapper.updateById(queue);
        }
    }

    private RegRetryQueueEntity findRetryQueue(Long tenantId, Long taskId) {
        LambdaQueryWrapper<RegRetryQueueEntity> wrapper = new LambdaQueryWrapper<RegRetryQueueEntity>();
        wrapper.eq(RegRetryQueueEntity::getTenantId, tenantId);
        wrapper.eq(RegRetryQueueEntity::getTaskId, taskId);
        return retryQueueMapper.selectOne(wrapper);
    }

    private List<RegFieldMappingEntity> loadFieldMappings(Long tenantId, String platformCode, String dataDomain) {
        LambdaQueryWrapper<RegFieldMappingEntity> wrapper = new LambdaQueryWrapper<RegFieldMappingEntity>();
        wrapper.eq(RegFieldMappingEntity::getTenantId, tenantId);
        wrapper.eq(RegFieldMappingEntity::getPlatformCode, platformCode.trim());
        wrapper.eq(RegFieldMappingEntity::getDataDomain, dataDomain.trim());
        wrapper.eq(RegFieldMappingEntity::getDeleted, 0);
        wrapper.eq(RegFieldMappingEntity::getEnabled, 1);
        return fieldMappingMapper.selectList(wrapper);
    }

    private List<RegCodeMappingEntity> loadCodeMappings(Long tenantId, String platformCode) {
        LambdaQueryWrapper<RegCodeMappingEntity> wrapper = new LambdaQueryWrapper<RegCodeMappingEntity>();
        wrapper.eq(RegCodeMappingEntity::getTenantId, tenantId);
        wrapper.eq(RegCodeMappingEntity::getPlatformCode, platformCode.trim());
        wrapper.eq(RegCodeMappingEntity::getDeleted, 0);
        wrapper.eq(RegCodeMappingEntity::getEnabled, 1);
        return codeMappingMapper.selectList(wrapper);
    }

    private RegReportTaskEntity requireTask(Long tenantId, Long taskId) {
        RegReportTaskEntity task = reportTaskMapper.selectById(taskId);
        if (task == null || !tenantId.equals(task.getTenantId())) {
            throw new BusinessException(404, "report task not found");
        }
        return task;
    }

    private RegReportReceiptVO toReceiptVO(RegReportReceiptEntity entity, Map<Long, String> taskNoCache) {
        RegReportReceiptVO vo = new RegReportReceiptVO();
        vo.setId(entity.getId());
        vo.setTenantId(entity.getTenantId());
        vo.setTaskId(entity.getTaskId());
        vo.setSuccess(entity.getSuccess() != null && entity.getSuccess() == 1);
        vo.setPlatformCode(entity.getPlatformCode());
        vo.setPlatformMessage(entity.getPlatformMessage());
        vo.setReceiptTime(entity.getReceiptTime());
        vo.setTaskNo(resolveTaskNo(entity.getTenantId(), entity.getTaskId(), taskNoCache));
        return vo;
    }

    private String resolveTaskNo(Long tenantId, Long taskId, Map<Long, String> cache) {
        if (cache.containsKey(taskId)) {
            return cache.get(taskId);
        }
        RegReportTaskEntity task = reportTaskMapper.selectById(taskId);
        String taskNo = task == null ? null : task.getTaskNo();
        cache.put(taskId, taskNo);
        return taskNo;
    }

    private RegReportTaskVO toTaskVO(RegReportTaskEntity entity) {
        RegReportTaskVO vo = new RegReportTaskVO();
        vo.setId(entity.getId());
        vo.setTenantId(entity.getTenantId());
        vo.setTaskNo(entity.getTaskNo());
        vo.setPlatformCode(entity.getPlatformCode());
        vo.setDataDomain(entity.getDataDomain());
        vo.setTriggerType(entity.getTriggerType());
        vo.setDataWindowStart(entity.getDataWindowStart());
        vo.setDataWindowEnd(entity.getDataWindowEnd());
        vo.setRecordCount(entity.getRecordCount());
        vo.setStatus(entity.getStatus());
        vo.setPayloadDigest(entity.getPayloadDigest());
        vo.setExecutedAt(entity.getExecutedAt());
        vo.setCreatedAt(entity.getCreatedAt());
        return vo;
    }

    private String generateTaskNo(Long tenantId) {
        return "REG-" + tenantId + "-" + DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS").format(LocalDateTime.now());
    }

    private String resolveTriggerType(String triggerType) {
        if (!StringUtils.hasText(triggerType)) {
            return RegTriggerType.MANUAL.name();
        }
        try {
            return RegTriggerType.valueOf(triggerType.trim().toUpperCase()).name();
        } catch (IllegalArgumentException e) {
            throw new BusinessException(400, "invalid triggerType: " + triggerType);
        }
    }

    private double calcRate(long numerator, long denominator) {
        if (denominator <= 0) {
            return 0D;
        }
        return Math.round(numerator * 10000.0 / denominator) / 100.0;
    }

    private void requireTenantId(Long tenantId) {
        if (tenantId == null || tenantId <= 0) {
            throw new BusinessException(400, "tenantId is invalid");
        }
    }
}
